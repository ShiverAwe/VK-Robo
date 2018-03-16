package apps

import temp.AuthData
import temp.Requests
import temp.UserIds

fun main(args: Array<String>) {
    val actor = AuthData.getActor()
    val data = Requests.groupsStatsForMultiUsers(actor,
            UserIds.shefer,
            UserIds.ovsyannikova)
    println(data.filter { it.value.size > 1 })
}