import com.vk.api.sdk.client.VkApiClient
import com.vk.api.sdk.client.actors.UserActor
import com.vk.api.sdk.httpclient.HttpTransportClient
import com.vk.api.sdk.queries.users.UserField

object Requests {

    val transportClient = HttpTransportClient.getInstance()
    val vk = VkApiClient(transportClient)

    fun groupsStatsForMultiUsers(actor: UserActor, vararg userIds: Int): Map<Int, Set<Int>> {
        val map = HashMap<Int, HashSet<Int>>()
        userIds.forEach { userId ->
            val userGroups: Set<Int> = vk
                    .groups()
                    .get(actor)
                    .userId(userId)
                    .execute()
                    .items
                    .toSet()
            userGroups.forEach { groupId ->
                map.getOrPut(groupId, { HashSet() }).add(userId)
            }
        }
        return map
    }


    fun userInfo(actor: UserActor, vararg userIds: String) {
        vk.users().get(actor)
                .userIds(userIds.toList())
                .fields(UserField.ABOUT,
                        UserField.CITY,
                        UserField.LAST_SEEN)
                .execute()
    }
}