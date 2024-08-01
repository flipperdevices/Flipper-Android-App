package com.flipperdevices.bridge.connection.feature.storageinfo.model

import com.flipperdevices.bridge.connection.feature.rpcinfo.model.FlipperInformationStatus

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
        is StorageStats.Loaded -> flash.free <= ENDING
        null -> false
    }
}
