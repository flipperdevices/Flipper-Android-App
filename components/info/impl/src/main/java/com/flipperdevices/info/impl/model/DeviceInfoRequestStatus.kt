package com.flipperdevices.info.impl.model

import com.flipperdevices.bridge.api.model.FlipperRequestRpcInformationStatus

data class DeviceInfoRequestStatus(
    val internalStorageRequestFinished: Boolean = false,
    val externalStorageRequestFinished: Boolean = false,
    val rpcDeviceInfoRequestFinished: Boolean = false
) {
    constructor(
        flipperRequestRpcInformationStatus: FlipperRequestRpcInformationStatus.InProgress
    ) : this(
        flipperRequestRpcInformationStatus.internalStorageRequestFinished,
        flipperRequestRpcInformationStatus.externalStorageRequestFinished,
        flipperRequestRpcInformationStatus.rpcDeviceInfoRequestFinished
    )
}
