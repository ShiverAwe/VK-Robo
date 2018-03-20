package temp

import com.vk.api.sdk.client.VkApiClient
import com.vk.api.sdk.client.actors.UserActor
import com.vk.api.sdk.exceptions.ApiException
import com.vk.api.sdk.exceptions.ApiTooManyException
import com.vk.api.sdk.exceptions.ClientException
import com.vk.api.sdk.httpclient.HttpTransportClient
import com.vk.api.sdk.queries.groups.GroupField
import java.lang.System.err
import java.util.*
import kotlin.collections.HashMap

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
        val users = try {
            tryApi {
                vk.users().get(actor)
                        .userIds(userIds.toList())
                        .execute()
            }
        } catch (e: ClientException) {
            if (userIds.size > 10) {
                val last = userIds.size - 1
                val middle = last / 2
                val userIdsPart1 = userIds.slice(IntRange(0, middle))
                val resultPart1 = userName(actor, *userIdsPart1.toTypedArray())
                val userIdsPart2 = userIds.slice(IntRange(middle + 1, last))
                val resultPart2 = userName(actor, *userIdsPart2.toTypedArray())
                val result = HashMap<Int, String>()
                result.putAll(resultPart1)
                result.putAll(resultPart2)
                return result
            } else {
                throw e
            }
        }
        return Utils.userListToMapById(users)
    }

    fun userListName(actor: UserActor, userIds: List<Any>): Map<Int, String> {
        return userName(actor, *userIds.map { it.toString() }.toTypedArray())
    }

    fun <T> tryApi(retryNTimes: Int = 1, action: () -> T): T {
        var triesLeft = retryNTimes
        var result: T? = null
        while (result == null) {
            try {
                result = action()
            } catch (e: ApiTooManyException) {
                Thread.sleep(200)
            } catch (e: ApiException) {
                if (triesLeft-- == 0) {
                    throw e
                }
            }
        }
        return result
    }

    fun <T> tryApiOrSkip(retryNTimes: Int = 1, action: () -> T): T? {
        return try {
            tryApi(retryNTimes, action)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}