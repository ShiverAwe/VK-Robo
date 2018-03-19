package apps

import temp.AuthData
import temp.Requests
import temp.UserIds
import java.util.*
import kotlin.collections.HashMap

fun main(args: Array<String>) {

    val code = AuthData.getCode()
    val actor = AuthData.getActor(code)
    val userIdA = UserIds.asMap["yovsyannikova"]!!
    val userIdB = UserIds.asMap["vshefer"]!!

    val total = HashMap(Requests.findLinks(actor, userIdA))

    val depth = 5

    for (i in 0..depth) {
        val linksNew: Map<Int, List<Int>> = Requests.findMoreLinks(actor, total)
        val linksNewUserIds: Set<Int> = linksNew.keys
        val userNames: Map<Int, String> = Requests.userListName(actor, linksNewUserIds.toList())

        val linksNewNames = HashMap<String, List<String>>()

        // Decrypt user ids into names
        linksNew.keys.forEach { userIdFrom ->
            val userIdsToNames = linksNew[userIdFrom]!!.map { userIdTo -> userNames[userIdTo]!! }
            linksNewNames[userNames[userIdFrom]!!] = userIdsToNames
        }

        linksNewNames.forEach { linkEntry ->
            println("""Link : ${linkEntry.key} : ${linkEntry.value}""")
        }

        if (linksNew.contains(userIdB)) {
            println(linksNew[userIdB])
            break
        } else {
            total.putAll(linksNew)
        }
        val scanner = Scanner(System.`in`)
        scanner.nextLine()
    }
}
