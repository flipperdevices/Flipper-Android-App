package com.flipperdevices.bridge.connection.transport.usb.impl.model

import android.content.Context
import android.hardware.usb.UsbManager
import com.flipperdevices.bridge.connection.transport.usb.api.FUSBDeviceConnectionConfig
import com.flipperdevices.core.di.AppGraph
import com.hoho.android.usbserial.driver.UsbSerialProber
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

@ContributesBinding(AppGraph::class, USBPlatformDeviceFactory::class)
class USBAndroidDeviceFactory @Inject constructor(
    private val context: Context
) : USBPlatformDeviceFactory {
    override fun getUSBPlatformDevice(
        config: FUSBDeviceConnectionConfig,
        scope: CoroutineScope
    ): Result<USBPlatformDevice> {
        val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
        val availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager)
        val serialDriver = availableDrivers.firstOrNull { driver ->
            driver.device.deviceId.toString() == config.path
        } ?: return Result.failure(
            USBDeviceNotFoundException("No USB device with id ${config.path}")
        )
        if (serialDriver.ports.isEmpty()) {
            return Result.failure(
                USBDeviceNotFoundException("USB device ${config.path} has no serial ports")
            )
        }
        val receiveBuffer = USBReceiveBuffer()
        return Result.success(
            USBAndroidDevice(
                serialDriver = serialDriver,
                usbManager = usbManager,
                permissionRequester = USBPermissionRequester(context, usbManager),
                receiveBuffer = receiveBuffer,
                serialListener = USBSerialListener(receiveBuffer)
            )
        )
    }
}
