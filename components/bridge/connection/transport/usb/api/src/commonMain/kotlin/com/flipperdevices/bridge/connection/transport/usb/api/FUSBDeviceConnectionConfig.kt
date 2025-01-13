package com.flipperdevices.bridge.connection.transport.usb.api

import com.flipperdevices.bridge.connection.transport.common.api.FDeviceConnectionConfig

data class FUSBDeviceConnectionConfig(
    val path: String,
) : FDeviceConnectionConfig<FUSBApi>()
