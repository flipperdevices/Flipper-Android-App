package com.flipperdevices.bridge.connection.transport.usb.impl

import com.flipperdevices.bridge.connection.feature.actionnotifier.api.FlipperActionNotifier
import com.flipperdevices.bridge.connection.transport.common.api.FInternalTransportConnectionStatus
import com.flipperdevices.bridge.connection.transport.common.api.FTransportConnectionStatusListener
import com.flipperdevices.bridge.connection.transport.usb.api.FUSBApi
import com.flipperdevices.bridge.connection.transport.usb.api.FUSBDeviceConnectionConfig
import com.flipperdevices.bridge.connection.transport.usb.api.USBDeviceConnectionApi
import com.flipperdevices.bridge.connection.transport.usb.impl.handshake.FlipperRpcHandshake
import com.flipperdevices.bridge.connection.transport.usb.impl.model.USBHandshakeTimeoutException
import com.flipperdevices.bridge.connection.transport.usb.impl.model.USBPlatformDevice
import com.flipperdevices.bridge.connection.transport.usb.impl.model.USBPlatformDeviceFactory
import com.flipperdevices.bridge.connection.transport.usb.impl.model.USBSerialParity
import com.flipperdevices.bridge.connection.transport.usb.impl.model.USBSerialPortParams
import com.flipperdevices.bridge.connection.transport.usb.impl.model.USBSerialStopBits
import com.flipperdevices.bridge.connection.transport.usb.impl.serial.FUSBSerialDeviceApi
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration.Companion.seconds

private val HANDSHAKE_TIMEOUT = 10.seconds
private val SERIAL_PORT_PARAMS = USBSerialPortParams(
    baudRate = 230400,
    dataBits = 8,
    stopBits = USBSerialStopBits.ONE,
    parity = USBSerialParity.NONE
)

class USBDeviceConnectionApiImpl(
    private val actionNotifierFactory: FlipperActionNotifier.Factory,
    private val usbPlatformDeviceFactory: USBPlatformDeviceFactory,
    private val rpcHandshake: FlipperRpcHandshake
) : USBDeviceConnectionApi, LogTagProvider {
    override val TAG = "USBDeviceConnectionApi"

    private fun closeDeviceOnScopeCancellation(scope: CoroutineScope, device: USBPlatformDevice) {
        scope.launch {
            try {
                awaitCancellation()
            } finally {
                withContext(NonCancellable) {
                    info { "Connection scope cancelled, closing port" }
                    device.close()
                }
            }
        }
    }

    private suspend fun runHandshakeWithTimeout(device: USBPlatformDevice): Result<ByteArray> {
        return try {
            withTimeout(HANDSHAKE_TIMEOUT) { rpcHandshake.enterRpcMode(device) }
        } catch (_: TimeoutCancellationException) {
            Result.failure(
                USBHandshakeTimeoutException(
                    "Flipper did not enter RPC mode within $HANDSHAKE_TIMEOUT"
                )
            )
        }
    }

    private fun createDeviceApi(
        scope: CoroutineScope,
        device: USBPlatformDevice,
        listener: FTransportConnectionStatusListener,
        initialData: ByteArray
    ): FUSBSerialDeviceApi = FUSBSerialDeviceApi(
        scope = scope,
        device = device,
        actionNotifier = actionNotifierFactory(scope),
        initialData = initialData,
        onTransportClosed = {
            listener.onStatusUpdate(FInternalTransportConnectionStatus.Disconnected)
        }
    )

    private suspend fun connectToDevice(
        scope: CoroutineScope,
        device: USBPlatformDevice,
        listener: FTransportConnectionStatusListener
    ): Result<FUSBApi> {
        closeDeviceOnScopeCancellation(scope, device)
        device.open(SERIAL_PORT_PARAMS).getOrElse { openError ->
            device.close()
            return Result.failure(openError)
        }
        return runHandshakeWithTimeout(device)
            .map { leftoverBytes -> createDeviceApi(scope, device, listener, leftoverBytes) }
            .onSuccess { deviceApi ->
                info { "USB transport connected" }
                listener.onStatusUpdate(
                    FInternalTransportConnectionStatus.Connected(scope, deviceApi)
                )
            }
            .onFailure { handshakeError ->
                info { "USB connect failed: $handshakeError" }
                device.close()
            }
    }

    override suspend fun connect(
        scope: CoroutineScope,
        config: FUSBDeviceConnectionConfig,
        listener: FTransportConnectionStatusListener
    ): Result<FUSBApi> {
        listener.onStatusUpdate(FInternalTransportConnectionStatus.Connecting)
        return usbPlatformDeviceFactory.getUSBPlatformDevice(config, scope).fold(
            onSuccess = { device -> connectToDevice(scope, device, listener) },
            onFailure = { deviceLookupError -> Result.failure(deviceLookupError) }
        )
    }
}
