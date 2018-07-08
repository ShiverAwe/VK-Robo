package temp

import com.vk.api.sdk.objects.users.UserXtrCounters
import java.util.*
import kotlin.collections.HashMap

object Utils {

    fun userListToMapById(users: MutableList<UserXtrCounters>): HashMap<Int, String> {
        val map = HashMap<Int, String>()
        users.forEach {
            map.put(it.id, "${it.firstName}_${it.lastName}")
        }
        return map
    }

    fun <K, V> printMapSorted(map: Map<K, V>, compare: ((v1: V, v2: V) -> Int)) {
        val entrylist: List<Map.Entry<K, V>> = map.entries.toList()
        Collections.sort(entrylist, { v1, v2 -> compare(v1.value, v2.value) })
        entrylist.forEach { println(it) }
    }

    fun extractWords(text: String): List<String> {
        return text
                .split("""[^a-zA-Z0-9а-яА-Я]""".toRegex())
                .filter { it.isNotEmpty() }
                .map { it.toLowerCase() }
    }
}