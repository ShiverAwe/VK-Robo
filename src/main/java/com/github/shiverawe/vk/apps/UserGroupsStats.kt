package com.github.shiverawe.vk.apps

import com.github.shiverawe.vk.temp.AuthData
import com.github.shiverawe.vk.temp.Requests
import com.github.shiverawe.vk.temp.UserIds
import com.github.shiverawe.vk.util.Utils
import com.vk.api.sdk.client.actors.UserActor
import com.vk.api.sdk.exceptions.ApiException
import com.vk.api.sdk.queries.groups.GroupField
import java.util.HashSet
import kotlin.collections.HashMap
import kotlin.collections.Map
import kotlin.collections.Set
import kotlin.collections.filter
import kotlin.collections.forEach
import kotlin.collections.getOrPut
import kotlin.collections.map
import kotlin.collections.toSet
import kotlin.collections.toTypedArray

fun main(args: Array<String>) {
    //val code = AuthData.getCode()
    val actor = AuthData.getActor()
    val users: IntArray = Requests.getFriendsOfUser(actor, UserIds.asMap["eneustroeva"]!!)
//    val users: IntArray = Requests.getMembersOfGroup(actor, "76477009")

//    val data = Requests.getGroupsOfUsers(actor, *UserIds.asMap.values.toIntArray())
    val data = getGroupsOfUsers(actor, *users)

    Utils.printMapSorted(data.filter { it.value.size > 1 }) { v1, v2 -> v2.size - v1.size }
}

/**
 * This method
 * 1. gets all groups of specified users
 * 2. creates a map, where key is a group name
 *    and value is list of users from specified users
 *    which are subscribed to that group
 */
fun getGroupsOfUsers(actor: UserActor, vararg userIds: Int): Map<String, Set<String>> {
    val map = HashMap<String, HashSet<String>>()
    val userNames: Map<Int, String> = Requests.userName(actor, *userIds.map { it.toString() }.toTypedArray())
    userIds.forEach { userId ->
        try {
            val userGroups: Set<String> = Requests.vk
                    .groups()
                    .getExtended(actor)
                    .userId(userId)
                    .fields(GroupField.SCREEN_NAME)
                    .execute()
                    .items
                    .map { it.screenName }
                    .toSet()
            userGroups.forEach { groupName ->
                userNames[userId]?.let {
                    map.getOrPut(groupName) { HashSet() }.add(it)
                }
            }
            Thread.sleep(500)
        } catch (e: ApiException) {
            System.err.println("Could not get groups of ${userNames[userId]}")
        } catch (e: Exception) {
            System.err.println("Some problem with user ${userNames[userId]}")
        }
    }
    return map
}
