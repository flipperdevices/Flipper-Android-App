package com.flipperdevices.bridge.api.manager.ktx.state

/**
 * Copy from https://github.com/NordicSemiconductor/Android-BLE-Library/blob/8fc0fcddba/ble-ktx/src/main/java/no/nordicsemi/android/ble/ktx/state/ConnectionState.kt
 */

sealed class ConnectionState {
    /** A connection to the device was initiated. */
    data object Connecting : ConnectionState()

    /** The device has connected and begun service discovery and initialization. */
    data object Initializing : ConnectionState()

    data object RetrievingInformation : ConnectionState()

    /** The initialization is complete, and the device is ready to use. */
    data class Ready(val supportedState: FlipperSupportedState) : ConnectionState()

    /** The disconnection was initiated. */
    data object Disconnecting : ConnectionState()

    /** The device disconnected or failed to connect. */
    data class Disconnected(val reason: Reason) : ConnectionState() {
        enum class Reason {
            SUCCESS,
            UNKNOWN,
            TERMINATE_LOCAL_HOST,
            TERMINATE_PEER_USER,
            LINK_LOSS,
            NOT_SUPPORTED,
            CANCELLED,
            TIMEOUT
        }

        /** Whether the device, that was connected using auto connect, has disconnected. */
        val isLinkLoss: Boolean
            get() = reason == Reason.LINK_LOSS

        /** Whether at least one required service was not found. */
        val isNotSupported: Boolean
            get() = reason == Reason.NOT_SUPPORTED

        /** Whether the connection timed out. */
        val isTimeout: Boolean
            get() = reason == Reason.TIMEOUT
    }

    /**
     * Whether the target device is connected, or not.
     */
    val isConnected: Boolean
        get() = this is Initializing || this is Ready

    /**
     * Whether the target device is ready to use.
     */
    val isReady: Boolean
        get() = this is Ready
}
