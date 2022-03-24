package com.flipperdevices.firstpair.impl.model

sealed class DevicePairState {
    object NotInitialized : DevicePairState()

    data class Connecting(val address: String?, val deviceName: String?) : DevicePairState()

    data class Connected(val address: String?, val deviceName: String?) : DevicePairState()

    object Timeout : DevicePairState()
}
