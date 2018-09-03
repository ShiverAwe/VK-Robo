package com.github.shiverawe.vk.apps

import com.github.shiverawe.vk.captcha.ManualVkCaptchaResolver
import com.github.shiverawe.vk.captcha.VkCaptchaResolver
import com.github.shiverawe.vk.temp.AuthData
import com.github.shiverawe.vk.temp.Requests
import com.github.shiverawe.vk.util.parseInts
import com.github.shiverawe.vk.util.retryOrSkip
import com.vk.api.sdk.client.actors.UserActor
import com.vk.api.sdk.exceptions.ApiCaptchaException
import com.vk.api.sdk.objects.friends.FriendStatusFriendStatus
import com.vk.api.sdk.objects.friends.FriendStatusFriendStatus.INCOMING_REQUEST
import com.vk.api.sdk.objects.friends.FriendStatusFriendStatus.NOT_A_FRIEND

fun main(args: Array<String>) {
    fun requiredInput(): String = readLine().orEmpty()
    val code = AuthData.getCode()
    val actor = AuthData.getActor(code)
    val usersTarget: List<Int> = requiredInput().parseInts()
    val usersExcluded: List<Int> = requiredInput().parseInts()
    val usersActive: List<Int> = usersTarget.subtract(usersExcluded).toList().reversed()
    val usersActiveStrangers = filterFriends(actor, usersActive, NOT_A_FRIEND, INCOMING_REQUEST)
    val usersSuccessfullyAdded: MutableList<Int> = ArrayList()

    var TTL = 30
    try {
        usersActiveStrangers.forEach { userId ->
            val added = addFriend(actor, userId)
            if (added) {
                usersSuccessfullyAdded.add(userId)
                TTL--
            }
            Thread.sleep(500)
        }
        if (TTL <= 0) throw RuntimeException("TTL ENDED")
    } catch (e: Exception) {
        e.printStackTrace()
    }
    println(usersSuccessfullyAdded)
    println("TTL $TTL")

}

val captchaResolver: VkCaptchaResolver = ManualVkCaptchaResolver()

/**
 * Return true, if friend has been added,
 * false otherwise.
 */
fun addFriend(actor: UserActor, userId: Int): Boolean {
    val captchaSid = try {
        Requests.vk.friends().add(actor, userId).execute()
        return true // if no captcha required.
    } catch (e: ApiCaptchaException) {
        e.sid
    } catch (e: Exception) {
        return false // Unknown exception
    }
    val captchaAnswer = captchaResolver.resolve(captchaSid)
    retryOrSkip {
        Requests.vk
                .friends()
                .add(actor, userId)
                .captchaKey(captchaAnswer)
                .captchaSid(captchaSid)
                .execute()
    }
    return true
}

fun filterFriends(actor: UserActor, userIds: List<Int>,
                  allowedStatus: FriendStatusFriendStatus,
                  vararg moreAllowedStatuses: FriendStatusFriendStatus): List<Int> {
    val allowedStatuses = moreAllowedStatuses.union(listOf(allowedStatus))
    val statuses = Requests.vk
            .friends()
            .areFriends(actor, userIds)
            .execute()
            .map { it.friendStatus }
    return userIds.zip(statuses)
            .filter { it.second in allowedStatuses }
            .map { it.first }
}
