package com.github.shiverawe.vk.captcha

interface VkCaptchaResolver {
    fun resolve(sid: String): String
}