package com.flipperdevices.info.impl.viewmodel.deviceinfo

import com.flipperdevices.bridge.api.model.StorageStats

data class FlipperStorageInformation(
    val internalStorageStats: FlipperInformationStatus<StorageStats?> =
        FlipperInformationStatus.NotStarted(),
    val externalStorageStats: FlipperInformationStatus<StorageStats?> =
        FlipperInformationStatus.NotStarted()
)