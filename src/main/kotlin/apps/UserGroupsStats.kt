package apps

import temp.AuthData
import temp.Requests
import temp.UserIds

fun main(args: Array<String>) {
    val actor = AuthData.getActor()
//    val friends: MutableList<Int> = Requests.vk
//            .friends()
//            .get(actor)
//            .execute()
//            .items

    val data = Requests.groupsStatsForMultiUsers(actor,
            UserIds.shefer,
            UserIds.ovsyannikova,
            UserIds.ashirov)
    println(data.filter { it.value.size > 1 })
}
