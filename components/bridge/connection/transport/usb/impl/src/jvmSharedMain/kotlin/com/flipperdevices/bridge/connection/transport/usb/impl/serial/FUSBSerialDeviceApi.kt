package com.flipperdevices.bridge.connection.transport.usb.impl.serial

import com.fazecast.jSerialComm.SerialPort
import com.flipperdevices.bridge.connection.feature.actionnotifier.api.FlipperActionNotifier
import com.flipperdevices.bridge.connection.transport.common.api.meta.FTransportMetaInfoApi
import com.flipperdevices.bridge.connection.transport.common.api.serial.FSerialDeviceApi
import com.flipperdevices.bridge.connection.transport.common.api.serial.FSerialRestartApi
import com.flipperdevices.bridge.connection.transport.common.api.serial.FlipperSerialSpeed
import com.flipperdevices.bridge.connection.transport.usb.api.FUSBApi
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

class FUSBSerialDeviceApi(
    private val scope: CoroutineScope,
    private val serialPort: SerialPort,
    private val actionNotifier: FlipperActionNotifier
) : FUSBApi, FSerialDeviceApi,
    FSerialRestartApi by NoopRestartApi(),
    FTransportMetaInfoApi by FakeTransportMetaInfoApi(),
    LogTagProvider {
    override val TAG = "FUSBSerialDeviceApi"

    private val receiverByteFlow = MutableSharedFlow<ByteArray>()
    private val txSpeed = SpeedMeter(scope)
    private val rxSpeed = SpeedMeter(scope)
    private val speedFlowState = MutableStateFlow(FlipperSerialSpeed())

    init {
        scope.launch {
            val buffer = ByteArray(size = 1024)
            var result = 1
            while (result > 0) {
                result = serialPort.readBytes(buffer, buffer.size)
                val readBytes = buffer.take(result).toByteArray()
                receiverByteFlow.emit(readBytes)
            }
            error("End loop with result $result")
        }
        combine(
            rxSpeed.getSpeed(),
            txSpeed.getSpeed()
        ) { rxBPS, txBPS ->
            actionNotifier.notifyAboutAction()
            speedFlowState.emit(
                FlipperSerialSpeed(receiveBytesInSec = rxBPS, transmitBytesInSec = txBPS)
            )
        }.launchIn(scope)
    }

    override suspend fun getSpeed() = speedFlowState.asStateFlow()
    override suspend fun getReceiveBytesFlow() = receiverByteFlow
    override fun getActionNotifier() = actionNotifier

    override suspend fun sendBytes(data: ByteArray) {
        var writtenBytesOffset = 0
        do {
            val writtenBytes =
                serialPort.writeBytes(data, data.size - writtenBytesOffset, writtenBytesOffset)
            info { "Write $writtenBytes" }
            if (writtenBytes == -1) {
                error("Failed to write bytes")
            }
            writtenBytesOffset += writtenBytes
        } while (writtenBytesOffset < data.size)
    }

    override suspend fun disconnect() {
        serialPort.closePort()
    }
}
