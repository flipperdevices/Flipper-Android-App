package com.flipperdevices.firstpair.impl.model

import com.flipperdevices.bridge.api.scanner.DiscoveredBluetoothDevice

sealed class DevicePairState {
    data object NotInitialized : DevicePairState()

    /**
     * A special state when we have not yet received a state from Android about the connection status,
     * but have initiated the connection from the app side
     */
    data class WaitingForStart(val address: String?, val deviceName: String?) : DevicePairState()

    data class Connecting(val address: String?, val deviceName: String?) : DevicePairState()

    data class Connected(val address: String?, val deviceName: String?) : DevicePairState()

    data class TimeoutConnecting(val discoveredBluetoothDevice: DiscoveredBluetoothDevice) :
        DevicePairState()

    data class TimeoutPairing(val discoveredBluetoothDevice: DiscoveredBluetoothDevice) :
        DevicePairState()
}
