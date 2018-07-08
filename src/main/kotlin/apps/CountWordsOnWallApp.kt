package apps

import com.vk.api.sdk.objects.wall.WallPostFull
import com.vk.api.sdk.queries.wall.WallGetFilter
import lib.Counter
import temp.AuthData
import temp.Requests
import temp.UserIds
import temp.Utils

fun main(args: Array<String>) {
    val code = AuthData.getCode()
    val actor = AuthData.getActor(code)
    val userId = UserIds.asMap["pkarachev"]

    val postsFull: MutableList<WallPostFull> = Requests.vk.wall()
            .get(actor)
            .ownerId(userId)
            .filter(WallGetFilter.OWNER)
            .execute()
            .items

    val texts = postsFull.map { it.text }

    var counterTotal = Counter<String>()

    texts.forEach { text ->
        val counter = Counter<String>()
        val values = Utils.extractWords(text).toTypedArray()
        counter.put(*values)
        counterTotal = counterTotal.merge(counter)
    }

    println(counterTotal)
}