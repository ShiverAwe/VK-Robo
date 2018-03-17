package apps

import temp.AuthData
import temp.Requests
import temp.UserIds
import temp.Utils

fun main(args: Array<String>) {
    val code = AuthData.getCode()
    val actor = AuthData.getActor(code)
    val serviceActor = AuthData.getServiceActor()
    val users: IntArray = Requests.getFriendsOfUser(actor, UserIds.asMap["eneustroeva"]!!)
//    val users: IntArray = Requests.getMembersOfGroup(actor, "76477009")

//    val data = Requests.getGroupsOfUsers(actor, *UserIds.asMap.values.toIntArray())
    val data = Requests.getGroupsOfUsers(actor, *users)

    Utils.printMapSorted(data.filter { it.value.size > 1 }, { v1, v2 -> v2.size - v1.size })
}

