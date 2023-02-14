package com.flipperdevices.info.impl.model

import androidx.compose.runtime.Stable
import com.flipperdevices.info.api.model.FlipperInformationStatus
import com.flipperdevices.info.api.model.FlipperStorageInformation
import com.flipperdevices.updater.model.FirmwareVersion

@Stable
data class FlipperBasicInfo(
    val firmwareVersion: FlipperInformationStatus<FirmwareVersion?> =
        FlipperInformationStatus.NotStarted(),
    val storageInfo: FlipperStorageInformation = FlipperStorageInformation()
)
