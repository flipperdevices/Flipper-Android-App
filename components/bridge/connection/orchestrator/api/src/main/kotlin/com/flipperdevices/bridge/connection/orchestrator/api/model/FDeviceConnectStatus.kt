package com.flipperdevices.bridge.connection.orchestrator.api.model

import com.flipperdevices.bridge.connection.config.api.model.FDeviceBaseModel
import com.flipperdevices.bridge.connection.transport.common.api.FConnectedDeviceApi

sealed class FDeviceConnectStatus {
    data class Disconnected(
        val device: FDeviceBaseModel?,
        val reason: DisconnectStatus
    ) : FDeviceConnectStatus()

    data class Connecting(
        val device: FDeviceBaseModel,
        val status: ConnectingStatus
    ) : FDeviceConnectStatus()

    data class Disconnecting(
        val device: FDeviceBaseModel
    ) : FDeviceConnectStatus()

    data class Connected(
        val device: FDeviceBaseModel,
        val deviceApi: FConnectedDeviceApi
    ) : FDeviceConnectStatus()
}
