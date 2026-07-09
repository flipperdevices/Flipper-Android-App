package com.flipperdevices.bridge.connection.transport.usb.impl.model

import com.fazecast.jSerialComm.SerialPort
import com.flipperdevices.bridge.connection.transport.usb.api.FUSBDeviceConnectionConfig
import com.flipperdevices.core.di.AppGraph
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import kotlinx.coroutines.CoroutineScope

@ContributesBinding(AppGraph::class, binding<USBPlatformDeviceFactory>())
class USBDesktopDeviceFactory @Inject constructor() : USBPlatformDeviceFactory {
    override fun getUSBPlatformDevice(
        config: FUSBDeviceConnectionConfig,
        scope: CoroutineScope
    ): USBPlatformDevice {
        return USBDesktopDevice(SerialPort.getCommPort(config.path))
    }
}
