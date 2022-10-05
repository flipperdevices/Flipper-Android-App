package com.flipperdevices.core.ktx.jre

import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.roundToInt

private const val PERCENT_MAX = 100

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

fun Float.roundPercentToString(): String {
    val processedPercent = if (this > 1.0f) 1.0f else if (this < 0.0f) 0.0f else this
    return "${(processedPercent * PERCENT_MAX).roundToInt()}%"
}

fun Int.length() = when (this) {
    0 -> 1
    else -> log10(abs(toDouble())).toInt() + 1
}

fun Number.roundToString(decimals: Int = 2): String {
    val pattern = "0.0${"#".repeat(decimals - 1)}"
    val decimalFormatter = DecimalFormat(pattern).apply {
        roundingMode = RoundingMode.FLOOR
    }
    return decimalFormatter.format(this)
}
