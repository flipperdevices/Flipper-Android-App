package com.flipperdevices.bridge.api.manager.delegates

import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
import kotlinx.coroutines.flow.Flow

interface FlipperConnectionInformationApi {
    fun isDeviceConnected(): Boolean
    fun getConnectionStateFlow(): Flow<ConnectionState>

    /**
     * @return null if device not connected
     */
    fun getConnectedDeviceName(): String?
}

fun ConnectionState.toHumanReadableString(): String {
    return when (this) {
        ConnectionState.Connecting -> "Connecting"
        ConnectionState.Initializing -> "Initializing"
        ConnectionState.RetrievingInformation -> "RetrievingInformation"
        is ConnectionState.Ready -> "Ready (supportedState=$supportedState)"
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
