package com.github.shiverawe.vk.apps

import com.github.shiverawe.vk.temp.AuthData
import com.github.shiverawe.vk.temp.Requests
import com.github.shiverawe.vk.util.parseInts
import com.vk.api.sdk.objects.friends.FriendStatusFriendStatus.*

fun main(args: Array<String>) {
    fun requiredInput(): String = readLine().orEmpty()
    val code = requiredInput()//AuthData.getCode()
    val actor = AuthData.getActor(code)
    val usersTarget: List<Int> = requiredInput().parseInts()
    val usersExcluded: List<Int> = requiredInput().parseInts()
    val usersActive: List<Int> = usersTarget.subtract(usersExcluded).toList().reversed()
    val usersSuccess: MutableList<Int> = ArrayList()


    var TTL = 30
    try {
        usersActive
                .forEach { userId ->
                    val friendStatus = Requests.vk
                            .friends()
                            .areFriends(actor, userId)
                            .execute()
                            .get(0)
                            .friendStatus
                    when (friendStatus) {
                        NOT_A_FRIEND, INCOMING_REQUEST -> {
                            Requests.vk
                                    .friends()
                                    .add(actor, userId)
                                    .execute()
                            println("ID $userId : Adding...")
                            TTL--
                            usersSuccess.add(userId)
                            Thread.sleep(5000)
                        }
                        IS_FRIEND -> println("ID $userId : Already friends")
                        OUTCOMING_REQUEST -> println("ID $userId : Outcoming friends")
                    }
                    Thread.sleep(400)
                }
        if (TTL <= 0) throw RuntimeException("TTL ENDED")
    } catch (e: Exception) {
        e.printStackTrace()
    }
    println(usersSuccess)
    println("TTL $TTL")

}


