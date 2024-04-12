package com.flipperdevices.bridge.connection.orchestrator.api.model

import com.flipperdevices.bridge.connection.common.api.FConnectedDeviceApi

sealed class FDeviceConnectStatus {
    data object Disconnected : FDeviceConnectStatus()
    data object Connecting : FDeviceConnectStatus()

    data class Connected(
        val deviceApi: FConnectedDeviceApi
    ) : FDeviceConnectStatus()
}
