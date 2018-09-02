package com.github.shiverawe.vk.apps

import com.github.shiverawe.vk.temp.AuthData
import com.github.shiverawe.vk.temp.Requests
import com.github.shiverawe.vk.util.parseList
import com.github.shiverawe.vk.util.retry
import com.vk.api.sdk.client.actors.UserActor

fun main(args: Array<String>) {
    val code = AuthData.getCode()
    val actor = AuthData.getActor(code)
    val groupdsIdsString = "klubrobotrek,clubrotor1,robokubpushkin,cttit,legocttit,school530,club61111027,sad25,podsolnushki25,teremok25sad,lyceum408,club6014654,schoolpushkin477,club101798127,school_552,club77642336,kolokolchik19,gruppaoduvanchiki19,rechevichok19,robotrek_3,club146656715,menarpushkin,moroshkaclub,detipushkina,cfkcsnews,samboolimp,public157578269,sunny_town,rpz_pushkinez,kraski_pushkin_cdt,club_maldetki,orbitalasertag,startjunior47,club45314678"
    val groupIds: List<String> = groupdsIdsString
            .parseList()
            .map { extractGroupId(it) }
    println(groupIds)

    val usersUnfiltered = arrayListOf<Int>()
    groupIds.forEach { groupId ->
        val groupUsers = retry(5) {
            getUsersOfGroup(groupId, actor)
                    .filter { usersUnfiltered.contains(it).not() }
        }
        usersUnfiltered.addAll(groupUsers)
    }

    val concurents = listOf(938031, 2478377, 409862588, 601884, 59516963, 95296305, 170607849, 74445068, 481063491, 636830, 7923467, 140772, 225831, 12878502, 43026082, 4767473, 120159, 2430147, 212830)
    val usersFiltered = usersUnfiltered.filter { !concurents.contains(it) }

    println(usersFiltered)
    //groupIds.map
}

fun getUsersOfGroup(groupId: String, actor: UserActor) = Requests.vk
        .groups()
        .getMembers(actor)
        .groupId(groupId)
        .execute()
        .items

fun extractGroupId(groupUrl: String): String {
    var realId = groupUrl
    listOf("club", "public").forEach { prefix ->
        if (realId.startsWith(prefix) && realId.substring(prefix.length).all { it in '0'..'9' }) {
            realId = realId.substring(prefix.length)
        }
    }
    return realId
}