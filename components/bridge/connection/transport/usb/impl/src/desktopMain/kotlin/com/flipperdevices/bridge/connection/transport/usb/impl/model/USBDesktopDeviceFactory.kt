package com.flipperdevices.bridge.connection.transport.usb.impl.model

import com.fazecast.jSerialComm.SerialPort
import com.flipperdevices.bridge.connection.transport.usb.api.FUSBDeviceConnectionConfig
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

@ContributesBinding(AppGraph::class, USBPlatformDeviceFactory::class)
class USBDesktopDeviceFactory @Inject constructor() : USBPlatformDeviceFactory {
    override fun getUSBPlatformDevice(
        config: FUSBDeviceConnectionConfig,
        scope: CoroutineScope
    ): USBPlatformDevice {
        return USBDesktopDevice(SerialPort.getCommPort(config.path))
    }
}
