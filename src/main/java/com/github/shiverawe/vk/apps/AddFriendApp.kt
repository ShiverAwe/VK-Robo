package com.github.shiverawe.vk.apps

import com.github.shiverawe.vk.temp.AuthData
import com.github.shiverawe.vk.temp.Requests
import com.github.shiverawe.vk.util.Utils
import com.github.shiverawe.vk.util.parseInts
import com.vk.api.sdk.client.actors.UserActor
import com.vk.api.sdk.exceptions.ApiCaptchaException
import com.vk.api.sdk.objects.friends.FriendStatusFriendStatus.*

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

fun addFriend(actor: UserActor, userId: Int): Boolean {
    val friendStatus = Requests.vk
            .friends()
            .areFriends(actor, userId)
            .execute()
            .get(0)
            .friendStatus
    @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
    return when (friendStatus) {
        NOT_A_FRIEND, INCOMING_REQUEST -> {
            val capcha = Utils.tryWithCapcha {
                Requests.vk
                        .friends()
                        .add(actor, userId)
                        .execute()
            }
            when (capcha) {
                null -> Unit
                else -> {
                    Requests.vk
                            .friends()
                            .add(actor, userId)
                            .captchaKey(capcha.key)
                            .captchaSid(capcha.sid)
                            .execute()
                }
            }
            true
        }
        IS_FRIEND, OUTCOMING_REQUEST -> false
    }
}