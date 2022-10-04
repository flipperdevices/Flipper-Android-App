package com.flipperdevices.info.impl.model

import com.flipperdevices.core.ktx.jre.round

private const val B = "B"
private const val KIB = "KiB"
private const val MIB = "MiB"
private const val GIB = "GiB"

private const val MULTIPLIER_BYTES = 1024.0
private const val KIBIBYTE_BYTES = MULTIPLIER_BYTES * MULTIPLIER_BYTES
private const val MIBIBYTE_BYTES = KIBIBYTE_BYTES * MULTIPLIER_BYTES

class StorageStateFormatter {
    fun formatFileSize(size: Long): String {
        return when {
            size < 0 -> "0 $B"
            size < MULTIPLIER_BYTES -> "${size.toDouble()} $B"
            size < KIBIBYTE_BYTES -> "${(size / MULTIPLIER_BYTES).round()} $KIB"
            size < MIBIBYTE_BYTES -> "${(size / KIBIBYTE_BYTES).round()} $MIB"
            else -> "${(size / MIBIBYTE_BYTES).round()} $GIB"
        }
    }
}
