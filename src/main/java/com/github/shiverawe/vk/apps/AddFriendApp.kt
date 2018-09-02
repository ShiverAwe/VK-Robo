package com.github.shiverawe.vk.apps

import com.github.shiverawe.vk.captcha.ManualVkCaptchaResolver
import com.github.shiverawe.vk.captcha.VkCaptchaResolver
import com.github.shiverawe.vk.temp.AuthData
import com.github.shiverawe.vk.temp.Requests
import com.github.shiverawe.vk.util.parseInts
import com.github.shiverawe.vk.util.retryOrSkip
import com.github.shiverawe.vk.util.tryOrCaptcha
import com.vk.api.sdk.client.actors.UserActor
import com.vk.api.sdk.exceptions.ApiCaptchaException
import com.vk.api.sdk.objects.friends.FriendStatusFriendStatus.INCOMING_REQUEST
import com.vk.api.sdk.objects.friends.FriendStatusFriendStatus.NOT_A_FRIEND

fun main(args: Array<String>) {
    fun requiredInput(): String = readLine().orEmpty()
    val code = AuthData.getCode()
    val actor = AuthData.getActor(code)
    val usersTarget: List<Int> = requiredInput().parseInts()
    val usersExcluded: List<Int> = requiredInput().parseInts()
    val usersActive: List<Int> = usersTarget.subtract(usersExcluded).toList().reversed()
    val usersSuccess: MutableList<Int> = ArrayList()

    var TTL = 30
    try {
        usersActive.forEach { userId ->
            val added = addFriend(actor, userId)
            if (added) {
                usersSuccess.add(userId)
                TTL--
            }
            Thread.sleep(500)
        }
        if (TTL <= 0) throw RuntimeException("TTL ENDED")
    } catch (e: ApiCaptchaException) {
        println("CAPCHA ${e.image} ${e.sid}")
        e.printStackTrace()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    println(usersSuccess)
    println("TTL $TTL")

}

val captchaResolver: VkCaptchaResolver = ManualVkCaptchaResolver()

fun addFriend(actor: UserActor, userId: Int): Boolean {
    val friendStatus = Requests.vk
            .friends()
            .areFriends(actor, userId)
            .execute()
            .get(0)
            .friendStatus
    return when (friendStatus) {
        NOT_A_FRIEND, INCOMING_REQUEST -> {
            val captchaSid =
                    tryOrCaptcha {
                        Requests.vk.friends().add(actor, userId).execute()
                    }
            when (captchaSid) {
                null -> Unit
                else -> retryOrSkip(5) {
                    Requests.vk
                            .friends()
                            .add(actor, userId)
                            .captchaKey(captchaResolver.resolve(captchaSid))
                            .captchaSid(captchaSid)
                            .execute()
                }
            }
            true
        }
        else -> false
    }
}
