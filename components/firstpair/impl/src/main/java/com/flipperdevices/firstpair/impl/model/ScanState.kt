package com.flipperdevices.firstpair.impl.model

import com.flipperdevices.bridge.api.scanner.DiscoveredBluetoothDevice

sealed class ScanState {
    open class Stopped : ScanState()

    object Searching : ScanState()

    class Founded(val devices: List<DiscoveredBluetoothDevice>) : ScanState()

    object Timeout : Stopped()
}
