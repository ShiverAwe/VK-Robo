package com.github.shiverawe.vk.temp

import com.github.shiverawe.vk.util.retry
import com.github.shiverawe.vk.util.userListToMapById
import com.vk.api.sdk.client.VkApiClient
import com.vk.api.sdk.client.actors.UserActor
import com.vk.api.sdk.exceptions.ClientException
import com.vk.api.sdk.httpclient.HttpTransportClient

object Requests {

    val transportClient = HttpTransportClient.getInstance()

    val vk = VkApiClient(transportClient)

    fun <K, T> List<T>.mapFor(mapping: T.() -> K): Map<K, T> {
        val map = HashMap<K, T>()
        forEach {
            map[it.mapping()] = it
        }
        return map
    }

    fun getFriendsOfUser(actor: UserActor, user: Int): IntArray {
        return Requests.vk
                .friends()
                .get(actor)
                .userId(user)
                .execute()
                .items.toIntArray()
    }

    fun userName(actor: UserActor, vararg userIds: String): Map<Int, String> {
        val users = try {
            retry {
                vk.users().get(actor)
                        .userIds(userIds.toList())
                        .execute()
            }
        } catch (e: ClientException) {
            if (userIds.size > 10) {
                val last = userIds.size - 1
                val middle = last / 2
                val userIdsPart1 = userIds.slice(IntRange(0, middle))
                val resultPart1 = userName(actor, *userIdsPart1.toTypedArray())
                val userIdsPart2 = userIds.slice(IntRange(middle + 1, last))
                val resultPart2 = userName(actor, *userIdsPart2.toTypedArray())
                val result = HashMap<Int, String>()
                result.putAll(resultPart1)
                result.putAll(resultPart2)
                return result
            } else {
                throw e
            }
        }
        return userListToMapById(users)
    }

    fun userListName(actor: UserActor, userIds: List<Any>): Map<Int, String> {
        return userName(actor, *userIds.map { it.toString() }.toTypedArray())
    }

}