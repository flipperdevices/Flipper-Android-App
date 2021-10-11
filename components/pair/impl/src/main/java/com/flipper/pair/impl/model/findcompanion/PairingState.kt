package com.flipper.pair.impl.model.findcompanion

import no.nordicsemi.android.ble.ktx.state.ConnectionState

sealed class PairingState {
    object NotInitialized : PairingState()
    object FindingDevice : PairingState()
    class Failed(val reason: String) : PairingState()
    class WithDevice(val connectionState: ConnectionState) : PairingState()
}
