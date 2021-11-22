package com.flipperdevices.bridge.api.manager.delegates

import kotlinx.coroutines.flow.StateFlow
import no.nordicsemi.android.ble.ktx.state.ConnectionState

interface FlipperConnectionInformationApi {
    fun isDeviceConnected(): Boolean
    fun getConnectionStateFlow(): StateFlow<ConnectionState>

    /**
     * @return null if device not connected
     */
    fun getConnectedDeviceName(): String?
}

fun ConnectionState.toHumanReadableString(): String {
    return when (this) {
        ConnectionState.Connecting -> "Connecting"
        ConnectionState.Initializing -> "Initializing"
        ConnectionState.Ready -> "Ready"
        ConnectionState.Disconnecting -> "Disconnecting"
        is ConnectionState.Disconnected -> "Disconnected (reason=${reason.toHumanReadableString()})"
    }
}

private fun ConnectionState.Disconnected.Reason.toHumanReadableString(): String {
    return when (this) {
        ConnectionState.Disconnected.Reason.SUCCESS -> "SUCCESS"
        ConnectionState.Disconnected.Reason.UNKNOWN -> "UNKNOWN"
        ConnectionState.Disconnected.Reason.TERMINATE_LOCAL_HOST -> "TERMINATE_LOCAL_HOST"
        ConnectionState.Disconnected.Reason.TERMINATE_PEER_USER -> "TERMINATE_PEER_USER"
        ConnectionState.Disconnected.Reason.LINK_LOSS -> "LINK_LOSS"
        ConnectionState.Disconnected.Reason.NOT_SUPPORTED -> "NOT_SUPPORTED"
        ConnectionState.Disconnected.Reason.CANCELLED -> "CANCELLED"
        ConnectionState.Disconnected.Reason.TIMEOUT -> "TIMEOUT"
    }
}
