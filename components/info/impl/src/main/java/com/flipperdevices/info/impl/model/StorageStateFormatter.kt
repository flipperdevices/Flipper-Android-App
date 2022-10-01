package com.flipperdevices.info.impl.model

import com.flipperdevices.core.ktx.jre.round

private const val B = "B"
private const val KIB = "KiB"
private const val MIB = "MiB"
private const val GIB = "GiB"

private const val BYTE = 1024.0
private const val KIBIBYTE = 1024.0 * BYTE
private const val MEGABYTE = 1024.0 * KIBIBYTE

class StorageStateFormatter {
    fun formatFileSize(size: Long): String {
        return when {
            size < 0 -> "0 $B"
            size < BYTE -> "${(size * 1.0).round()} $B"
            size < KIBIBYTE -> "${(size / BYTE).round()} $KIB"
            size < MEGABYTE -> "${(size / KIBIBYTE).round()} $MIB"
            else -> "${(size / MEGABYTE).round()} $GIB"
        }
    }
}
