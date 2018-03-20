package temp

import com.vk.api.sdk.client.actors.UserActor
import java.util.*
import kotlin.collections.HashMap

class UserTree(
        val actor: UserActor,
        val root: Int
) {
    val total = HashMap<Int, List<Int>>()
    val rounds = HashMap<Int, Map<Int, List<Int>>>()
    val names = HashMap<Int, String>()
    var round: Int = 0

    /**
     * On round 0 we have only one link from root user to himself
     */
    init {
        val map0 = HashMap<Int, List<Int>>()
        map0[root] = Arrays.asList(root)
        rounds[0] = map0
    }

    fun nextRound() {
        val userIds = rounds[round]!!.keys.toList()
        round++
        val foundLinks = findLinks(actor, userIds)
        rounds[round] = foundLinks
        // Downlowd names for new found friends
        names.putAll(Requests.userListName(actor, foundLinks.keys.toList()))
        total.putAll(foundLinks)
    }

    private fun findLinks(actor: UserActor, userIds: List<Int>): Map<Int, List<Int>> {
        val result = HashMap<Int, ArrayList<Int>>()
        val alreadyFound = total.keys
        userIds.forEach { userId ->
            Requests.tryApiOrSkip {
                Requests.getFriendsOfUser(actor, userId)
                        .filter { friendId ->
                            !alreadyFound.contains(friendId)
                        }.forEach { friendId ->
                            val link = ArrayList(total[userId])
                            link.add(friendId)
                            result[friendId] = link
                        }
            }
        }
        return result
    }


}