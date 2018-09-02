package com.github.shiverawe.vk.captcha


import com.github.shiverawe.vk.temp.Configuration
import org.apache.http.HttpEntity
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.utils.URIBuilder
import org.apache.http.entity.mime.HttpMultipartMode
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.apache.http.entity.mime.content.FileBody
import org.apache.http.impl.client.DefaultHttpClient
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption


val rucaptchaKey = Configuration.rucaptchaKey

class AutomaticVkCaptchaResolver : VkCaptchaResolver {
    override fun resolve(sid: String): String {
        val imageUrl = "https://api.vk.com/captcha.php?sid=$sid&s=1"
        val path = "src/main/resources/captcha.jpg"
        val captchaFile = downloadCaptchaImage(imageUrl, path)
        val requestId = uploadToRucaptcha(captchaFile)
        Thread.sleep(5000) // Wait rucaptcha solve captcha
        return {
            receiveAnswerFromRucaptcha(requestId)
        }.retry(times = 10, cooldown = 2000) // If rucapthca did not solve in 5 sec
    }

}

fun downloadCaptchaImage(urlFrom: String, pathTo: String): File {
    URL(urlFrom).openStream().use {
        val path: Path = Paths.get(pathTo)
        println(path.toAbsolutePath())
        Files.copy(it, path, StandardCopyOption.REPLACE_EXISTING)
        return path.toFile()
    }
}

fun uploadToRucaptcha(file: File): String {

    val url = "http://rucaptcha.com/in.php"

    val multipart: HttpEntity = MultipartEntityBuilder.create().apply {
        setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
        addTextBody("key", rucaptchaKey)
        addPart("file", FileBody(file))
    }.build()

    val httpPost = HttpPost(url).apply {
        entity = multipart
    }

    return DefaultHttpClient()
            .execute(httpPost)
            .entity.content
            .bufferedReader().lines()
            .reduce(String::plus)
            .get().let {
                if (it.startsWith("OK|")) {
                    it.substring(3)
                } else {
                    throw IllegalStateException("Rucaptcha returned `$it` status")
                }
            }
}

fun receiveAnswerFromRucaptcha(requestId: String): String {
    val url = "http://rucaptcha.com/res.php"
    val builder = URIBuilder(url)
            .setParameter("key", rucaptchaKey)
            .setParameter("action", "get")
            .setParameter("id", requestId)
    val httpGet = HttpGet(builder.build())

    val response = DefaultHttpClient().execute(httpGet)

    return response.entity.content.bufferedReader().lines().reduce(String::plus).get().let {
        if (it.startsWith("OK|")) {
            it.substring(3)
        } else {
            throw IllegalStateException("Rucaptcha returned `$it` status")
        }
    }
}

private fun <T> (() -> T).retry(times: Int = 1, cooldown: Long = 0): T {
    var left = times
    while (true) {
        try {
            return this()
        } catch (e: Exception) {
            if (left-- < 0) throw e
            Thread.sleep(cooldown)
        }
    }
}

fun main(args: Array<String>) {
    val answer = AutomaticVkCaptchaResolver().resolve("123456")
    println(answer)
}