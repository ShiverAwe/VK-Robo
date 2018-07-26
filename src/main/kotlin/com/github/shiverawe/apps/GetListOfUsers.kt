package com.github.shiverawe.apps

import com.github.shiverawe.temp.AuthData
import com.github.shiverawe.temp.Requests
import com.github.shiverawe.temp.UserIds


fun main(args: Array<String>) {
    val code = AuthData.getCode()
    val actor = AuthData.getActor(code)
    val users: IntArray = Requests.getFriendsOfUser(actor, UserIds.asMap["eneustroeva"]!!)
//    val users: IntArray = Requests.getMembersOfGroup(actor, "76477009")
    val result = Requests.vk
            .groups()
            .getMembers(actor)
            .groupId("45091870") // Temniy ugolok
            .execute()
    println(result.count)
}