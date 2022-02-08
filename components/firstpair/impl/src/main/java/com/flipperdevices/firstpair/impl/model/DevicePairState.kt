package com.flipperdevices.firstpair.impl.model

sealed class DevicePairState {
    object NotInitialized : DevicePairState()

    data class Connecting(val address: String?) : DevicePairState()

    class Connected(val address: String?) : DevicePairState()
}
