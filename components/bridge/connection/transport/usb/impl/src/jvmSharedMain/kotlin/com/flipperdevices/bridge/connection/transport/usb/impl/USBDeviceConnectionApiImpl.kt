package com.flipperdevices.bridge.connection.transport.usb.impl

import com.flipperdevices.bridge.connection.feature.actionnotifier.api.FlipperActionNotifier
import com.flipperdevices.bridge.connection.transport.common.api.FInternalTransportConnectionStatus
import com.flipperdevices.bridge.connection.transport.common.api.FTransportConnectionStatusListener
import com.flipperdevices.bridge.connection.transport.usb.api.FUSBApi
import com.flipperdevices.bridge.connection.transport.usb.api.FUSBDeviceConnectionConfig
import com.flipperdevices.bridge.connection.transport.usb.api.USBDeviceConnectionApi
import com.flipperdevices.bridge.connection.transport.usb.impl.model.USBPlatformDevice
import com.flipperdevices.bridge.connection.transport.usb.impl.model.USBPlatformDeviceFactory
import com.flipperdevices.bridge.connection.transport.usb.impl.serial.FUSBSerialDeviceApi
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val FLOOD_END_STRING = "\r\n\r\n>: ".toByteArray()
private val COMMAND = "start_rpc_session\r".toByteArray()
private const val BAUD_RATE = 230400
private const val DATA_BITS = 8

class USBDeviceConnectionApiImpl(
    private val actionNotifierFactory: FlipperActionNotifier.Factory,
    private val usbPlatformDeviceFactory: USBPlatformDeviceFactory
) : USBDeviceConnectionApi, LogTagProvider {
    override val TAG = "USBDeviceConnectionApi"

    override suspend fun connect(
        scope: CoroutineScope,
        config: FUSBDeviceConnectionConfig,
        listener: FTransportConnectionStatusListener
    ): Result<FUSBApi> = runCatching {
        listener.onStatusUpdate(FInternalTransportConnectionStatus.Connecting)
        val serialPort = usbPlatformDeviceFactory.getUSBPlatformDevice(config, scope)
        val portOpened = serialPort.connect(
            BAUD_RATE,
            DATA_BITS,
            USBPlatformDevice.ONE_STOP_BIT,
            USBPlatformDevice.NO_PARITY
        )

        info { "Read port is: $portOpened" }

        if (!portOpened) {
            error("Fail to open port")
        }

        scope.launch {
            try {
                awaitCancellation()
            } finally {
                withContext(NonCancellable) {
                    info { "Closing port..." }
                    serialPort.closePort()
                }
            }
        }

        info { "Port opened, start reading flood" }
        skipFlood(serialPort, FLOOD_END_STRING)
        info { "Flood skipped, send start_rpc_session command" }
        writeFully(serialPort, COMMAND)
        skipFlood(serialPort, "\n".toByteArray())
        info { "Flood skipped. Now we are in RPC mode" }

        val deviceApi = FUSBSerialDeviceApi(
            scope = scope,
            serialPort = serialPort,
            actionNotifier = actionNotifierFactory(scope)
        )
        info { "Finish create device API" }

        listener.onStatusUpdate(FInternalTransportConnectionStatus.Connected(scope, deviceApi))

        return@runCatching deviceApi
    }

    private fun writeFully(serialPort: USBPlatformDevice, data: ByteArray) {
        var writtenOffset = 0
        while (writtenOffset < data.size) {
            val writtenBytes =
                serialPort.writeBytes(data, data.size - writtenOffset, writtenOffset)
            if (writtenBytes <= 0) {
                error("Failed to write bytes, result is $writtenBytes")
            }
            writtenOffset += writtenBytes
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun skipFlood(serialPort: USBPlatformDevice, floodBytes: ByteArray) {
        info { "Start wait flood" }
        var floodCurrentIndex = 0
        val buffer = ByteArray(size = 1)
        while (!Thread.interrupted()) {
            val readCount = serialPort.readBytes(buffer, buffer.size)
            if (readCount < 0) {
                error("Port closed while waiting for flood end")
            }
            if (readCount == 0) {
                continue
            }

            info {
                "#skipFlood Read ${buffer.toHexString()} (${
                    buffer.joinToString {
                        it.toInt().toChar().toString()
                    }
                })"
            }
            if (floodBytes[floodCurrentIndex] == buffer.first()) {
                floodCurrentIndex++
            } else {
                floodCurrentIndex = 0
            }
            if (floodCurrentIndex == floodBytes.size) {
                return
            }
        }
    }
}
