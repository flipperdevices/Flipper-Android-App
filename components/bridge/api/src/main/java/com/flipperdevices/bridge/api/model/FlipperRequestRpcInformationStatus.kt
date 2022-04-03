package com.flipperdevices.bridge.api.model

sealed class FlipperRequestRpcInformationStatus {
    object NotStarted : FlipperRequestRpcInformationStatus()
    data class InProgress(
        val internalStorageRequestFinished: Boolean = false,
        val externalStorageRequestFinished: Boolean = false,
        val rpcDeviceInfoRequestFinished: Boolean = false
    ) : FlipperRequestRpcInformationStatus()
}
