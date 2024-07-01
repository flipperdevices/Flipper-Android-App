package com.flipperdevices.bridge.service.impl.model

/**
 * An Exception occurs when we realise that for some reason the device has changed its mac address
 */
data class DeviceChangedMacException(
    val oldMacAddress: String,
    val newMacAddress: String
) : RuntimeException()
