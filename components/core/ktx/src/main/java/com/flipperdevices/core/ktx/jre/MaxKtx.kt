package com.flipperdevices.core.ktx.jre

fun <K, T : Comparable<T>> Map<K, T>.getMaxOf(vararg keys: K, default: T): T {
    if (keys.size == 1) {
        return this[keys.first()] ?: default
    }
    var maxValue = this[keys.first()] ?: return default
    keys.forEachIndexed { index, key ->
        if (index == 0) { // First element skip
            return@forEachIndexed
        }
        val value = this[key] ?: return@forEachIndexed
        if (value > maxValue) {
            maxValue = value
        }
    }

    return maxValue
}
