package com.flipperdevices.bridge.connection.configbuilder.impl.builders

import com.flipperdevices.bridge.connection.transport.usb.api.FUSBDeviceConnectionConfig
import javax.inject.Inject

class FlipperZeroUsbBuilderConfig @Inject constructor() {
    fun build(
        portDescription: String
    ) = FUSBDeviceConnectionConfig(portDescription)
}