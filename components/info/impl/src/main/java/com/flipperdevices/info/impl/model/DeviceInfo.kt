package com.flipperdevices.info.impl.model

import android.content.Context
import com.flipperdevices.bridge.api.model.StorageStats
import com.flipperdevices.info.impl.R
import com.flipperdevices.updater.model.FirmwareVersion
import kotlin.math.max

private const val ENDING = 20 * 1024 // 20 Kb

data class DeviceInfo(
    val firmwareVersion: FirmwareVersion? = null,
    val flashInt: StorageStats? = null,
    val flashSd: StorageStats? = null
) {
    fun isIntStorageEnding(): Boolean = isStorageEnding(flashInt)
    fun isExtStorageEnding(): Boolean = isStorageEnding(flashSd)

    private fun isStorageEnding(flash: StorageStats?): Boolean {
        return when (flash) {
            StorageStats.Error -> false
            is StorageStats.Loaded -> {
                flash.free <= ENDING
            }
            null -> false
        }
    }
}

fun StorageStats.toString(context: Context): String {
    return when (this) {
        StorageStats.Error -> {
            context.getString(R.string.info_device_info_flash_not_found)
        }
        is StorageStats.Loaded -> {
            val formatter = StorageStateFormatter()
            val usedHumanReadable = formatter.formatFileSize(
                max(0L, total - free)
            )
            val totalHumanReadable = formatter.formatFileSize(total)

            "$usedHumanReadable / $totalHumanReadable"
        }
    }
}

fun StorageStats?.toString(): Pair<String, String> {
    return when (this) {
        StorageStats.Error, null -> "-" to "-"
        is StorageStats.Loaded -> "$free" to "$total"
    }
}
