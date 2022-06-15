package com.flipperdevices.core.ktx.jre

/**
 * @return int if long fit in Int.MIN_VALUE..Int.MAX_VALUE.
 * If not, return Int.MIN_VALUE or Int.MAX_VALUE
 */
fun Long.toIntSafe(): Int {
    if (this > Int.MAX_VALUE) {
        return Int.MAX_VALUE
    }

    if (this < Int.MIN_VALUE) {
        return Int.MIN_VALUE
    }

    return this.toInt()
}
