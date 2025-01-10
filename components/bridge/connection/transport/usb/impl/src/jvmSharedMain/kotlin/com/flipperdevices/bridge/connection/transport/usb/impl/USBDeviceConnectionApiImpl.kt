package com.flipperdevices.bridge.connection.transport.usb.impl

import com.fazecast.jSerialComm.SerialPort
import com.flipperdevices.bridge.connection.feature.actionnotifier.api.FlipperActionNotifier
import com.flipperdevices.bridge.connection.transport.common.api.FTransportConnectionStatusListener
import com.flipperdevices.bridge.connection.transport.usb.api.FUSBApi
import com.flipperdevices.bridge.connection.transport.usb.api.FUSBDeviceConnectionConfig
import com.flipperdevices.bridge.connection.transport.usb.api.USBDeviceConnectionApi
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
private const val OPEN_PORT_TIME_MS = 1000

class USBDeviceConnectionApiImpl(
    private val actionNotifierFactory: FlipperActionNotifier.Factory
) : USBDeviceConnectionApi, LogTagProvider {
    override val TAG = "USBDeviceConnectionApi"

    override suspend fun connect(
        scope: CoroutineScope,
        config: FUSBDeviceConnectionConfig,
        listener: FTransportConnectionStatusListener
    ): Result<FUSBApi> = runCatching {
        val serialPort = SerialPort.getCommPort(config.path)
        serialPort.setComPortParameters(
            BAUD_RATE,
            DATA_BITS,
            SerialPort.ONE_STOP_BIT,
            SerialPort.NO_PARITY
        )
        serialPort.openPort(OPEN_PORT_TIME_MS)
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0)
        val portOpened = serialPort.openPort()

        info { "Read port is: $portOpened" }

        if (!portOpened) {
            error("Fail to open port")
        }

        skipFlood(serialPort, FLOOD_END_STRING)
        serialPort.writeBytes(COMMAND, COMMAND.size)
        skipFlood(serialPort, "\n".toByteArray())

        val deviceApi = FUSBSerialDeviceApi(
            scope = scope,
            serialPort = serialPort,
            actionNotifier = actionNotifierFactory(scope)
        )

        scope.launch {
            try {
                awaitCancellation()
            } finally {
                withContext(NonCancellable) {
                    serialPort.closePort()
                }
            }
        }
        return@runCatching deviceApi
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun skipFlood(serialPort: SerialPort, floodBytes: ByteArray) {
        info { "Start wait flood" }
        var floodCurrentIndex = 0
        val buffer = ByteArray(size = 1)
        while (!Thread.interrupted()) {
            if (serialPort.readBytes(buffer, buffer.size) == 0) {
                info { "Exit from skipFlood because buffer is empty" }
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
