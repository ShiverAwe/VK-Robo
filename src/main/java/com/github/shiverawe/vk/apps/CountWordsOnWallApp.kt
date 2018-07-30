package com.github.shiverawe.vk.apps

import com.github.shiverawe.vk.lib.Counter
import com.github.shiverawe.vk.temp.AuthData
import com.github.shiverawe.vk.temp.Requests
import com.github.shiverawe.vk.temp.UserIds
import com.github.shiverawe.vk.temp.Utils
import com.vk.api.sdk.objects.wall.WallPostFull
import com.vk.api.sdk.queries.wall.WallGetFilter

fun main(args: Array<String>) {
    val code = AuthData.getCode()
    val actor = AuthData.getActor(code)
    val userId = UserIds.asMap["pkarachev"]

    val postsFull: MutableList<WallPostFull> = Requests.vk.wall()
            .get(actor)
            .ownerId(userId)
            .filter(WallGetFilter.OWNER)
            .execute()
            .items

    val texts = postsFull.map { it.text }

    var counterTotal = Counter<String>()

    texts.forEach { text ->
        val counter = Counter<String>()
        val values = Utils.extractWords(text).toTypedArray()
        counter.put(*values)
        counterTotal = counterTotal.merge(counter)
    }

    println(counterTotal)
}