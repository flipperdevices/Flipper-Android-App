package com.flipperdevices.bridge.connection.transport.common.api

fun interface FTransportConnectionStatusListener {
    fun onStatusUpdate(status: FInternalTransportConnectionStatus)
}
