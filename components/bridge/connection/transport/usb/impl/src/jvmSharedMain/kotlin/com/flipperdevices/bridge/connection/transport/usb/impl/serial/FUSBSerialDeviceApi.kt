package com.flipperdevices.bridge.connection.transport.usb.impl.serial

import com.flipperdevices.bridge.connection.feature.actionnotifier.api.FlipperActionNotifier
import com.flipperdevices.bridge.connection.transport.common.api.meta.FTransportMetaInfoApi
import com.flipperdevices.bridge.connection.transport.common.api.serial.FSerialDeviceApi
import com.flipperdevices.bridge.connection.transport.common.api.serial.FSerialRestartApi
import com.flipperdevices.bridge.connection.transport.common.api.serial.FlipperSerialSpeed
import com.flipperdevices.bridge.connection.transport.usb.api.FUSBApi
import com.flipperdevices.bridge.connection.transport.usb.impl.model.USBPlatformDevice
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

class FUSBSerialDeviceApi(
    private val scope: CoroutineScope,
    private val device: USBPlatformDevice,
    private val actionNotifier: FlipperActionNotifier,
    private val initialData: ByteArray,
    private val onTransportClosed: () -> Unit
) : FUSBApi,
    FSerialDeviceApi,
    FSerialRestartApi by NoopRestartApi(),
    FTransportMetaInfoApi by FakeTransportMetaInfoApi(),
    LogTagProvider {
    override val TAG = "FUSBSerialDeviceApi"

    private val receiverByteFlow = MutableSharedFlow<ByteArray>()
    private val txSpeed = SpeedMeter(scope)
    private val rxSpeed = SpeedMeter(scope)
    private val speedFlowState = MutableStateFlow(FlipperSerialSpeed())

    init {
        scope.launch { readUntilPortClosed() }
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

    private suspend fun emitReceivedBytes(chunk: ByteArray) {
        rxSpeed.onReceiveBytes(chunk.size)
        receiverByteFlow.emit(chunk)
    }

    private suspend fun readUntilPortClosed() {
        if (initialData.isNotEmpty()) {
            // The shared flow drops emissions that happen before the first
            // subscriber appears, so leftover handshake bytes must wait for it.
            receiverByteFlow.subscriptionCount.first { subscribers -> subscribers > 0 }
            emitReceivedBytes(initialData)
        }
        while (true) {
            val chunk = device.read().getOrElse { readError ->
                info { "USB port closed, stopping read loop: $readError" }
                onTransportClosed()
                return
            }
            emitReceivedBytes(chunk)
        }
    }

    override suspend fun getSpeed() = speedFlowState.asStateFlow()

    override suspend fun getReceiveBytesFlow() = receiverByteFlow

    override fun getActionNotifier() = actionNotifier

    override suspend fun sendBytes(data: ByteArray) {
        // FSerialDeviceApi contract has no Result channel, so a write
        // failure intentionally propagates as an exception.
        device.write(data)
            .onSuccess { txSpeed.onReceiveBytes(data.size) }
            .getOrThrow()
    }

    override suspend fun disconnect() {
        device.close()
    }
}
