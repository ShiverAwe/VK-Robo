package com.github.shiverawe.vk.captcha

import java.awt.Desktop
import java.net.URL

class ManualVkCaptchaResolver : VkCaptchaResolver {
    override fun resolve(sid: String): String {
        val image = "https://api.vk.com/captcha.php?sid=$sid&s=1"
        Desktop.getDesktop().browse(URL(image).toURI())
        return readLine().orEmpty()
    }
}