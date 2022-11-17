package com.flipperdevices.info.impl.model

sealed class DeviceStatus {
    object NoDevice : DeviceStatus()
    data class NoDeviceInformation(
        val deviceName: String,
        val connectInProgress: Boolean
    ) : DeviceStatus()

    data class Connected(
        val deviceName: String,
        val batteryLevel: Float,
        val isCharging: Boolean
    ) : DeviceStatus()

    fun getFlipperName(): String = when (this) {
        is Connected -> deviceName
        NoDevice -> ""
        is NoDeviceInformation -> deviceName
    }
}
