package com.flipperdevices.info.impl.model.deviceinfo

import android.content.Context
import com.flipperdevices.core.ktx.jre.toFormattedSize
import com.flipperdevices.info.api.model.FlipperInformationStatus
import com.flipperdevices.info.api.model.FlipperStorageInformation
import com.flipperdevices.info.api.model.StorageStats
import com.flipperdevices.info.impl.R
import kotlin.math.max

private const val ENDING = 20 * 1024 // 20 Kb

val FlipperStorageInformation.internalStorageRequestInProgress: Boolean
    get() = internalStorageStatus is FlipperInformationStatus.InProgress
val FlipperStorageInformation.externalStorageRequestInProgress: Boolean
    get() = externalStorageStatus is FlipperInformationStatus.InProgress
val FlipperStorageInformation.flashIntStats: StorageStats?
    get() = (internalStorageStatus as? FlipperInformationStatus.Ready)?.data
val FlipperStorageInformation.flashSdStats: StorageStats?
    get() = (externalStorageStatus as? FlipperInformationStatus.Ready)?.data

fun FlipperStorageInformation.isIntStorageEnding(): Boolean = isStorageEnding(flashIntStats)
fun FlipperStorageInformation.isExtStorageEnding(): Boolean = isStorageEnding(flashSdStats)

private fun isStorageEnding(flash: StorageStats?): Boolean {
    return when (flash) {
        StorageStats.Error -> false
        is StorageStats.Loaded -> {
            flash.free <= ENDING
        }
        null -> false
    }
}

fun StorageStats.toString(context: Context): String {
    return when (this) {
        StorageStats.Error -> {
            context.getString(R.string.info_device_info_flash_not_found)
        }
        is StorageStats.Loaded -> {
            val usedHumanReadable = max(0L, total - free).toFormattedSize()
            val totalHumanReadable = total.toFormattedSize()

            "$usedHumanReadable / $totalHumanReadable"
        }
    }
}
