package com.flipperdevices.info.impl.model

import com.flipperdevices.bridge.api.model.FlipperRequestRpcInformationStatus

data class DeviceInfoRequestStatus(
    val internalStorageRequestInProgress: Boolean = false,
    val externalStorageRequestInProgress: Boolean = false,
    val rpcDeviceInfoRequestInProgress: Boolean = false
) {
    constructor(
        flipperRequestRpcInformationStatus: FlipperRequestRpcInformationStatus.InProgress
    ) : this(
        !flipperRequestRpcInformationStatus.internalStorageRequestFinished,
        !flipperRequestRpcInformationStatus.externalStorageRequestFinished,
        !flipperRequestRpcInformationStatus.rpcDeviceInfoRequestFinished
    )
}
