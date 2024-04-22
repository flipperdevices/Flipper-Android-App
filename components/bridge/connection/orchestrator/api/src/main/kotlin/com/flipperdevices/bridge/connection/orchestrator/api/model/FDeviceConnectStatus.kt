package com.flipperdevices.bridge.connection.orchestrator.api.model

import com.flipperdevices.bridge.connection.transport.common.api.FConnectedDeviceApi

sealed class FDeviceConnectStatus {
    data class Disconnected(val reason: DisconnectStatus) : FDeviceConnectStatus()
    data class Connecting(val status: ConnectingStatus) : FDeviceConnectStatus()
    data object Disconnecting : FDeviceConnectStatus()

    data class Connected(
        val deviceApi: FConnectedDeviceApi
    ) : FDeviceConnectStatus()
}
