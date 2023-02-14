package com.flipperdevices.info.api.model

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
