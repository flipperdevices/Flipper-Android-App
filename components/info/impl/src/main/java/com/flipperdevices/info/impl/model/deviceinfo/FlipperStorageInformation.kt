package com.flipperdevices.info.impl.model.deviceinfo

import androidx.compose.runtime.Stable
import com.flipperdevices.info.impl.viewmodel.deviceinfo.helpers.FlipperInformationStatus

private const val ENDING = 20 * 1024 // 20 Kb

@Stable
data class FlipperStorageInformation(
    val internalStorageStatus: FlipperInformationStatus<StorageStats?> =
        FlipperInformationStatus.NotStarted(),
    val externalStorageStatus: FlipperInformationStatus<StorageStats?> =
        FlipperInformationStatus.NotStarted()
)

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