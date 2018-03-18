package temp

import com.vk.api.sdk.client.VkApiClient
import com.vk.api.sdk.client.actors.UserActor
import com.vk.api.sdk.exceptions.ApiException
import com.vk.api.sdk.httpclient.HttpTransportClient
import com.vk.api.sdk.queries.groups.GroupField
import java.lang.System.err

object Requests {

    val transportClient = HttpTransportClient.getInstance()

    val vk = VkApiClient(transportClient)

    /**
     * This method
     * 1. gets all groups of specified users
     * 2. creates a map, where key is a group name
     *    and value is list of users from specified users
     *    which are subscribed to that group
     */
    fun getGroupsOfUsers(actor: UserActor, vararg userIds: Int): Map<String, Set<String>> {
        val map = HashMap<String, HashSet<String>>()
        val userNames: Map<Int, String> = userName(actor, *userIds.map { it.toString() }.toTypedArray())
        userIds.forEach { userId ->
            try {
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
                Thread.sleep(500)
            } catch (e: ApiException) {
                err.println("Could not get groups of ${userNames[userId]}")
            } catch (e: Exception) {
                err.println("Some problem with user ${userNames[userId]}")
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

    fun getFriendsOfUser(actor: UserActor, user: Int): IntArray {
        return Requests.vk
                .friends()
                .get(actor)
                .userId(user)
                .execute()
                .items.toIntArray()
    }

    fun userName(actor: UserActor, vararg userIds: String): Map<Int, String> {
        val users = vk.users().get(actor)
                .userIds(userIds.toList())
                .execute()
        return Utils.userListToMapById(users)
    }
}