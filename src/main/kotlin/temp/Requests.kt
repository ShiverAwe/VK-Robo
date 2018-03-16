package temp

import com.vk.api.sdk.client.VkApiClient
import com.vk.api.sdk.client.actors.UserActor
import com.vk.api.sdk.httpclient.HttpTransportClient
import com.vk.api.sdk.queries.groups.GroupField

object Requests {

    val transportClient = HttpTransportClient.getInstance()

    val vk = VkApiClient(transportClient)

    fun groupsStatsForMultiUsers(actor: UserActor, vararg userIds: Int): Map<String, Set<String>> {
        val map = HashMap<String, HashSet<String>>()
        val userNames: Map<Int, String> = userName(actor, *userIds.map { it.toString() }.toTypedArray())
        userIds.forEach { userId ->
            val userGroups: Set<String> = vk
                    .groups()
                    .getExtended(actor)
                    .userId(userId)
                    .fields(GroupField.SCREEN_NAME)
                    .execute()
                    .items
                    .map { it.screenName }
                    .toSet()
            userGroups.forEach { groupName ->
                userNames[userId]?.let {
                    map.getOrPut(groupName, { HashSet() }).add(it)
                }
            }
        }
        return map
    }

    fun <K, T> List<T>.mapFor(action: T.() -> K): Map<K, T> {
        val map = HashMap<K, T>()
        forEach {
            map.put(it.action(), it)
        }
        return map
    }

    fun userName(actor: UserActor, vararg userIds: String): Map<Int, String> {
        val users = vk.users().get(actor)
                .userIds(userIds.toList())
                .execute()
        return Utils.userListToMapById(users)
    }
}