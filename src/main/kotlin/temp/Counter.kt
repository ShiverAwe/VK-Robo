package temp

class Counter<T>(
        private val map: HashMap<T, Int> = HashMap()
) {

    fun contains(value: T): Boolean {
        return map.containsKey(value)
    }

    fun put(vararg values: T) {
        values.forEach { value ->
            map[value] = map.getOrDefault(value, 0) + 1
        }
    }

    fun map(): Map<T, Int> {
        return HashMap<T, Int>(map)
    }

    fun get(value: T): Int {
        return map.getOrDefault(value, 0)
    }

    fun merge(that: Counter<T>): Counter<T> {
        val mapResult = HashMap<T, Int>()
        val values = this.map.keys.toMutableSet()
        values.addAll(that.map.keys)
        values.forEach { value ->
            mapResult[value] = this.get(value) + that.get(value)
        }
        return Counter(mapResult)
    }

    fun print() {
        map.entries
                .sortedByDescending { it.value }
                .forEach { entry ->
                    println("""${entry.key} : ${entry.value}""")
                }
    }
}