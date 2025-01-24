package com.flipperdevices.bridge.connection.transport.usb.impl.model

import com.flipperdevices.bridge.connection.transport.usb.api.FUSBDeviceConnectionConfig
import kotlinx.coroutines.CoroutineScope

interface USBPlatformDeviceFactory {
    fun getUSBPlatformDevice(
        config: FUSBDeviceConnectionConfig,
        scope: CoroutineScope
    ): USBPlatformDevice
}
