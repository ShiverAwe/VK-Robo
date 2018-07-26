package com.github.shiverawe.lib

/**
 * This class counts equal elements, given to it.
 */
class Counter<T>(
        private val map: HashMap<T, Int> = HashMap()
) {

    operator fun get(value: T): Int {
        return map.getOrDefault(value, 0)
    }

    fun put(vararg values: T) {
        values.forEach { value ->
            map[value] = this[value] + 1
        }
    }

    fun contains(value: T): Boolean {
        return map.containsKey(value)
    }

    fun merge(that: Counter<T>): Counter<T> {
        val mapResult = HashMap<T, Int>()
        val values = this.map.keys.toMutableSet()
        values.addAll(that.map.keys)
        values.forEach { value ->
            mapResult[value] = this[value] + that[value]
        }
        return Counter(mapResult)
    }

    fun entries(): List<Map.Entry<T, Int>> = map.entries.sortedByDescending { it.value }

    override fun toString(): String {
        var string: String = ""
        entries()
                .map { entry -> """{${entry.key}: ${entry.value}}""" }
                .forEach { string += it }
        return "{$string}"
    }
}