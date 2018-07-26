package com.github.shiverawe.apps

import com.github.shiverawe.temp.AuthData
import com.github.shiverawe.temp.Requests


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