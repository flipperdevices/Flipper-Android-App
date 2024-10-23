package com.flipperdevices.bridge.rpcinfo.model

import android.content.Context
import com.flipperdevices.bridge.rpcinfo.api.R
import com.flipperdevices.core.ktx.jre.toFormattedSize
import kotlin.math.max

data class FlipperStorageInformation(
    val internalStorageStatus: FlipperInformationStatus<StorageStats?> =
        FlipperInformationStatus.NotStarted(),
    val externalStorageStatus: FlipperInformationStatus<StorageStats?> =
        FlipperInformationStatus.NotStarted()
)

fun <T> FlipperInformationStatus<T>.dataOrNull() = when (this) {
    is FlipperInformationStatus.InProgress -> data
    is FlipperInformationStatus.Ready -> data
    is FlipperInformationStatus.NotStarted -> null
}

private const val ENDING = 20 * 1024 // 20 Kb

val FlipperStorageInformation.externalStorageRequestInProgress: Boolean
    get() = externalStorageStatus is FlipperInformationStatus.InProgress
val FlipperStorageInformation.flashIntStats: StorageStats?
    get() = (internalStorageStatus as? FlipperInformationStatus.Ready)?.data
val FlipperStorageInformation.flashSdStats: StorageStats?
    get() = (externalStorageStatus as? FlipperInformationStatus.Ready)?.data

fun FlipperStorageInformation.isExtStorageEnding(): Boolean = isStorageEnding(flashSdStats)

private fun isStorageEnding(flash: StorageStats?): Boolean {
    return when (flash) {
        StorageStats.Error -> false
        is StorageStats.Loaded -> flash.free <= ENDING
        null -> false
    }
}

fun StorageStats.toString(context: Context): String {
    return when (this) {
        StorageStats.Error -> context.getString(R.string.info_device_info_flash_not_found)
        is StorageStats.Loaded -> {
            val usedHumanReadable = max(0L, total - free).toFormattedSize()
            val totalHumanReadable = total.toFormattedSize()

            "$usedHumanReadable / $totalHumanReadable"
        }
    }
}
