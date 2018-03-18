package temp

class Counter<T> {
    val map = HashMap<T, Int>()

    fun contains(value: T): Boolean {
        return map.containsKey(value)
    }

    fun put(vararg values: T) {
        values.forEach { value ->
            if (contains(value)) {
                map[value] = 1
            } else {
                map[value] = map[value]!! + 1
            }
        }
    }

    fun get(): HashMap<T, Int> {
        return map
    }

}