package com.flipperdevices.info.impl.model

import androidx.compose.runtime.Stable
import com.flipperdevices.info.impl.model.deviceinfo.FlipperStorageInformation
import com.flipperdevices.info.impl.viewmodel.deviceinfo.helpers.FlipperInformationStatus
import com.flipperdevices.updater.model.FirmwareVersion

@Stable
data class FlipperBasicInfo(
    val firmwareVersion: FlipperInformationStatus<FirmwareVersion?> =
        FlipperInformationStatus.NotStarted(),
    val storageInfo: FlipperStorageInformation = FlipperStorageInformation()
)
