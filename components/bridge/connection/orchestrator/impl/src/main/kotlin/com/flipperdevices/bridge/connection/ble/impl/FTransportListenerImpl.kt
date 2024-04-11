package com.flipperdevices.bridge.connection.ble.impl

import com.flipperdevices.bridge.connection.common.api.FInternalTransportConnectionStatus
import com.flipperdevices.bridge.connection.common.api.FTransportConnectionStatusListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FTransportListenerImpl : FTransportConnectionStatusListener {
    private val transportState = MutableStateFlow<FInternalTransportConnectionStatus>(
        FInternalTransportConnectionStatus.Disconnected
    )

    fun getState() = transportState.asStateFlow()

    override fun onStatusUpdate(status: FInternalTransportConnectionStatus) {
        transportState.update { status }
    }
}
