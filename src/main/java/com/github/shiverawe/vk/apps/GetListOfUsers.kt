package com.github.shiverawe.vk.apps

import com.github.shiverawe.vk.temp.AuthData
import com.github.shiverawe.vk.temp.Requests

fun main(args: Array<String>) {
    val code = AuthData.getCode()
    val actor = AuthData.getActor(code)
    val result = Requests.vk
            .groups()
            .getMembers(actor)
            .groupId("45091870") // Temniy ugolok
            .execute()
    println(result.count)
}