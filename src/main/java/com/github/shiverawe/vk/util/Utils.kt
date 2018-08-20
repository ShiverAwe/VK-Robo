package com.github.shiverawe.vk.util

import com.github.shiverawe.vk.model.Capcha
import com.vk.api.sdk.exceptions.ApiCaptchaException
import com.vk.api.sdk.exceptions.ApiException
import com.vk.api.sdk.exceptions.ApiTooManyException
import com.vk.api.sdk.objects.users.UserXtrCounters
import java.awt.Desktop
import java.net.URL
import java.util.*
import kotlin.collections.HashMap

object Utils {

    fun userListToMapById(users: MutableList<UserXtrCounters>): HashMap<Int, String> {
        val map = HashMap<Int, String>()
        users.forEach {
            map.put(it.id, "${it.firstName}_${it.lastName}")
        }
        return map
    }

    fun <K, V> printMapSorted(map: Map<K, V>, compare: ((v1: V, v2: V) -> Int)) {
        val entrylist: List<Map.Entry<K, V>> = map.entries.toList()
        Collections.sort(entrylist, { v1, v2 -> compare(v1.value, v2.value) })
        entrylist.forEach { println(it) }
    }

    fun extractWords(text: String): List<String> {
        return text
                .split("""[^a-zA-Z0-9а-яА-Я]""".toRegex())
                .filter { it.isNotEmpty() }
                .map { it.toLowerCase() }
    }


    fun <T> retry(retryNTimes: Int = 1, action: () -> T): T {
        var triesLeft = retryNTimes
        var result: T? = null
        while (result == null) {
            try {
                result = action()
            } catch (e: ApiTooManyException) {
                Thread.sleep(200)
            } catch (e: ApiException) {
                if (triesLeft-- == 0) {
                    throw e
                }
            }
        }
        return result
    }

    fun <T> retryOrSkip(retryNTimes: Int = 1, action: () -> T): T? {
        return try {
            retry(retryNTimes, action)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun tryWithCapcha(action: () -> Unit): Capcha? {
        return try {
            action()
            null
        } catch (e: ApiCaptchaException) {
            Desktop.getDesktop().browse(URL(e.image).toURI())
            return Capcha(key = readLine()!!, sid = e.sid)
        }
    }
}