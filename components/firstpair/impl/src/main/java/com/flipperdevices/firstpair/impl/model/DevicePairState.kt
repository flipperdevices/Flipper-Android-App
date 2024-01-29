package com.flipperdevices.firstpair.impl.model

import com.flipperdevices.bridge.api.scanner.DiscoveredBluetoothDevice

sealed class DevicePairState {
    object NotInitialized : DevicePairState()

    data class Connecting(val address: String?, val deviceName: String?) : DevicePairState()

    data class Connected(val address: String?, val deviceName: String?) : DevicePairState()

    class TimeoutConnecting(val discoveredBluetoothDevice: DiscoveredBluetoothDevice) :
        DevicePairState()

    class TimeoutPairing(val discoveredBluetoothDevice: DiscoveredBluetoothDevice) :
        DevicePairState()
}
