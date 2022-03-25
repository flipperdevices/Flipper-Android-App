package com.flipperdevices.info.impl.model

sealed class DeviceStatus {
    object NoDevice : DeviceStatus()
    data class NotConnected(val deviceName: String) : DeviceStatus()
    data class Connected(
        val deviceName: String,
        val batteryLevel: Float
    ) : DeviceStatus()
}
