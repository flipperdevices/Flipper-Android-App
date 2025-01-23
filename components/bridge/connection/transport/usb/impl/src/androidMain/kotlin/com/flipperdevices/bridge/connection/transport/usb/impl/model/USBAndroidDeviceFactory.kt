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
    ): USBPlatformDevice {
        val manager = context.getSystemService(Context.USB_SERVICE) as UsbManager
        val availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager)
        val device = availableDrivers.filter {
            it.device.deviceId.toString() == config.path
        }.firstOrNull()
        if (device == null) {
            error("Failed find device with id ${config.path}")
        }

        return USBAndroidDevice(
            serialDriver = device,
            usbManager = manager,
            scope = scope
        )
    }
}