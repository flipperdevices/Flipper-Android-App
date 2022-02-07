package com.flipperdevices.firstpair.impl.model

import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState

sealed class PairingState {
    object NotInitialized : PairingState()
    object FindingDevice : PairingState()
    class Failed(val reason: String) : PairingState()
    class WithDevice(val connectionState: ConnectionState) : PairingState()
}
