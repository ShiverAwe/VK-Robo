package apps

import temp.AuthData
import temp.Requests
import temp.UserIds
import temp.Utils

fun main(args: Array<String>) {
    val code = AuthData.getCode()//"e87ea11ddd2529da92"
//    val token = "39ecb0b5ae166ad30809479a79e57c1519f4208972794e51499f7656bb03c074640e415d8baf68685b1e5"
    val actor = AuthData.getActor(code)
//    val serviceActor = AuthData.getServiceActor()
//    val friends: IntArray = Requests.vk
//            .friends()
//            .get(serviceActor)
//            .userId(UserIds.asMap["vshefer"])
//            .execute()
//            .items.toIntArray()

    val data = Requests.groupsStatsForMultiUsers(actor, *UserIds.asMap.values.toIntArray())

    Utils.printMapSorted(data.filter { it.value.size > 1 }, { v1, v2 -> v2.size - v1.size })
}
