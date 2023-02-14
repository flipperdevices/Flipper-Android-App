package com.flipperdevices.bridge.api.model

sealed class FlipperRequestRpcInformationStatus {
    object NotStarted : FlipperRequestRpcInformationStatus()
    data class InProgress(
        val rpcDeviceInfoRequestFinished: Boolean = false
    ) : FlipperRequestRpcInformationStatus()
}
