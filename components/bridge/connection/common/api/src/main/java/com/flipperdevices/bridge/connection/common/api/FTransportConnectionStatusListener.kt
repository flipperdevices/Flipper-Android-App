package com.flipperdevices.bridge.connection.common.api

fun interface FTransportConnectionStatusListener {
    fun onStatusUpdate(status: FInternalTransportConnectionStatus)
}
