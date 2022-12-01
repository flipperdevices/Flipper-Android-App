package com.flipperdevices.core.ktx.jre

private const val B = "B"
private const val KIB = "KiB"
private const val MIB = "MiB"
private const val GIB = "GiB"

private const val MULTIPLIER_BYTES = 1024.0
private const val KIBIBYTE_BYTES = MULTIPLIER_BYTES * MULTIPLIER_BYTES
private const val MIBIBYTE_BYTES = KIBIBYTE_BYTES * MULTIPLIER_BYTES

fun Number.toFormattedSize(): String {
    val size = this.toLong()
    return when {
        size < 0 -> "0 $B"
        size < MULTIPLIER_BYTES -> "${size.roundToString()} $B"
        size < KIBIBYTE_BYTES -> "${(size / MULTIPLIER_BYTES).roundToString()} $KIB"
        size < MIBIBYTE_BYTES -> "${(size / KIBIBYTE_BYTES).roundToString()} $MIB"
        else -> "${(size / MIBIBYTE_BYTES).roundToString()} $GIB"
    }
}
