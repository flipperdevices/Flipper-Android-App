package com.flipperdevices.bridge.connection.common.api

sealed class FInternalTransportConnectionStatus {
    data object Disconnected : FInternalTransportConnectionStatus()

    data object Connecting : FInternalTransportConnectionStatus()

    data object Pairing : FInternalTransportConnectionStatus()

    data object Connected : FInternalTransportConnectionStatus()
    data object Disconnecting : FInternalTransportConnectionStatus()

    data class Error(val throwable: Throwable) : FInternalTransportConnectionStatus()
}
