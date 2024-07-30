package com.flipperdevices.bridge.connection.transport.common.api

import kotlinx.coroutines.CoroutineScope

sealed class FInternalTransportConnectionStatus {
    data object Disconnected : FInternalTransportConnectionStatus()

    data object Connecting : FInternalTransportConnectionStatus()

    data object Pairing : FInternalTransportConnectionStatus()

    data class Connected(
        val scope: CoroutineScope,
        val deviceApi: FConnectedDeviceApi
    ) : FInternalTransportConnectionStatus()

    data object Disconnecting : FInternalTransportConnectionStatus()
}
