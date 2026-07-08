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
    ): Result<USBPlatformDevice> {
        val serialPort = runCatching { SerialPort.getCommPort(config.path) }
            .getOrElse { portLookupError ->
                return Result.failure(
                    USBDeviceNotFoundException(
                        "No serial port at path ${config.path}",
                        portLookupError
                    )
                )
            }
        return Result.success(
            USBDesktopDevice(
                serialPort = serialPort,
                scope = scope,
                receiveBuffer = USBReceiveBuffer()
            )
        )
    }
}
