package com.github.shiverawe.vk.apps

import com.github.shiverawe.vk.temp.AuthData
import com.github.shiverawe.vk.temp.UserIds
import com.github.shiverawe.vk.temp.UserTree

fun main(args: Array<String>) {

    val code = AuthData.getCode()
    val actor = AuthData.getActor(code)
    val userIdA = UserIds.asMap["yovsyannikova"]!!
    val userIdB = UserIds.asMap["vshefer"]!!

    val tree = UserTree(actor, userIdA)



    tree.nextRound()

    tree.print()

    tree.nextRound()

    tree.print()


}
