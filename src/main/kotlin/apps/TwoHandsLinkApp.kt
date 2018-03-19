package apps

import temp.AuthData
import temp.Requests
import temp.UserIds

fun main(args: Array<String>) {

    val code = AuthData.getCode()
    val actor = AuthData.getActor(code)
    val userIdA = UserIds.asMap["yovsyannikova"]!!
    val userIdB = UserIds.asMap["vshefer"]!!

    val total = HashMap(Requests.findLinks(actor, userIdA))

    while (true) {
        val linksNew: Map<Int, List<Int>> = Requests.findMoreLinks(actor, total)
        val linksNewUserIds: Set<Int> = linksNew.keys
        val linksNewUserNames: Map<Int, String> = Requests.userListName(actor, linksNewUserIds.toList())

        linksNew.forEach { linkEntry ->
            println("""Link : ${linkEntry.key} : ${linkEntry.value}""")
        }

        if (linksNew.contains(userIdB)) {
            println(linksNew[userIdB])
            break
        } else {
            total.putAll(linksNew)
        }
    }

}