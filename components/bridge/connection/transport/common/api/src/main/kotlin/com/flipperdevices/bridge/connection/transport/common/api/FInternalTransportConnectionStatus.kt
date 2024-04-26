package com.flipperdevices.bridge.connection.transport.common.api

sealed class FInternalTransportConnectionStatus {
    data object Disconnected : FInternalTransportConnectionStatus()

    data object Connecting : FInternalTransportConnectionStatus()

    data object Pairing : FInternalTransportConnectionStatus()

    data class Connected(
        val deviceApi: FConnectedDeviceApi
    ) : FInternalTransportConnectionStatus()

    data object Disconnecting : FInternalTransportConnectionStatus()
}
