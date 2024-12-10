package com.flipperdevices.bridge.connection.feature.getinfo.model

data class FGattInformation(
    val deviceName: String? = null,
    val manufacturerName: String? = null,
    val batteryLevel: Float? = null,
    val isCharging: Boolean = false
)