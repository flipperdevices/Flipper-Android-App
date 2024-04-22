package com.flipperdevices.bridge.connection.ble.impl

import com.flipperdevices.bridge.connection.transport.common.api.FInternalTransportConnectionStatus
import com.flipperdevices.bridge.connection.transport.common.api.FTransportConnectionStatusListener
import com.flipperdevices.bridge.connection.orchestrator.api.model.ConnectingStatus
import com.flipperdevices.bridge.connection.orchestrator.api.model.DisconnectStatus
import com.flipperdevices.bridge.connection.orchestrator.api.model.FDeviceConnectStatus
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FTransportListenerImpl : FTransportConnectionStatusListener, LogTagProvider {
    override val TAG = "FTransportListener"

    private val state = MutableStateFlow<FDeviceConnectStatus>(
        FDeviceConnectStatus.Disconnected(DisconnectStatus.NOT_INITIALIZED)
    )

    fun getState() = state.asStateFlow()

    fun onErrorDuringConnect(throwable: Throwable) {
        when (throwable) {
            else -> {
                error(throwable) { "Unknown error from transport layer" }
                state.update { FDeviceConnectStatus.Disconnected(DisconnectStatus.ERROR_UNKNOWN) }
            }
        }
    }

    override fun onStatusUpdate(status: FInternalTransportConnectionStatus) {
        state.update { currentStatus ->
            when (status) {
                is FInternalTransportConnectionStatus.Connected -> FDeviceConnectStatus.Connected(
                    status.deviceApi
                )

                FInternalTransportConnectionStatus.Connecting ->
                    FDeviceConnectStatus.Connecting(ConnectingStatus.CONNECTING)

                FInternalTransportConnectionStatus.Disconnected ->
                    if (currentStatus is FDeviceConnectStatus.Disconnected) {
                        currentStatus
                    } else {
                        FDeviceConnectStatus.Disconnected(DisconnectStatus.REPORTED_BY_TRANSPORT)
                    }

                FInternalTransportConnectionStatus.Disconnecting -> FDeviceConnectStatus.Disconnecting
                FInternalTransportConnectionStatus.Pairing ->
                    FDeviceConnectStatus.Connecting(ConnectingStatus.INITIALIZING)
            }
        }
    }
}
