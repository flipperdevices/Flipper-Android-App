package com.flipperdevices.info.impl.model

import androidx.compose.runtime.Stable
import com.flipperdevices.bridge.rpcinfo.model.FlipperInformationStatus
import com.flipperdevices.bridge.rpcinfo.model.FlipperStorageInformation
import com.flipperdevices.updater.model.FirmwareVersion

@Stable
data class FlipperBasicInfo(
    val firmwareVersion: FlipperInformationStatus<FirmwareVersion?> =
        FlipperInformationStatus.NotStarted(),
    val storageInfo: FlipperStorageInformation = FlipperStorageInformation()
)
