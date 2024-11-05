package com.flipperdevices.bridge.connection.transport.ble.impl.serial

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import com.flipperdevices.bridge.connection.feature.actionnotifier.api.FlipperActionNotifier
import com.flipperdevices.bridge.connection.transport.ble.impl.model.BLEConnectionPermissionException
import com.flipperdevices.bridge.connection.transport.common.api.serial.FSerialDeviceApi
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import no.nordicsemi.android.kotlin.ble.client.main.service.ClientBleGattCharacteristic
import no.nordicsemi.android.kotlin.ble.core.data.util.DataByteArray
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

// How much time we next command
// Small size increase count of ble packet
// Large size increase waiting time for each command
private const val SERIAL_SEND_WAIT_TIMEOUT_MS = 100L

class FSerialOverflowThrottler @AssistedInject constructor(
    @Assisted private val serialApi: FSerialDeviceApi,
    @Assisted private val scope: CoroutineScope,
    @Assisted private val overflowCharacteristic: ClientBleGattCharacteristic,
    @Assisted private val flipperActionNotifier: FlipperActionNotifier,
    private val context: Context
) : FSerialDeviceApi,
    LogTagProvider {
    override val TAG = "FlipperSerialOverflowThrottler"

    private val channel = Channel<ByteArray>()

    private var pendingBytes: ByteArray? = null

    override fun getActionNotifier() = flipperActionNotifier

    /**
     * Bytes waiting to be sent to the device
     */
    private val bufferSizeState = MutableSharedFlow<Int>(replay = 1)

    init {
        scope.launch {
            if (
                context.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT)
                != PackageManager.PERMISSION_GRANTED
            ) {
                throw BLEConnectionPermissionException()
            }
            updateRemainingBuffer(overflowCharacteristic.read())
            overflowCharacteristic
                .getNotifications(bufferOverflow = BufferOverflow.SUSPEND)
                .collect {
                    updateRemainingBuffer(it)
                }
        }
        scope.launch {
            bufferSizeState.collectLatest { bufferSize ->
                sendCommandsWhileBufferNotEnd(bufferSize)
            }
        }
    }

    override suspend fun getSpeed() = serialApi.getSpeed()

    override suspend fun getReceiveBytesFlow() = serialApi.getReceiveBytesFlow()

    override suspend fun sendBytes(data: ByteArray) {
        channel.send(data)
    }

    private suspend fun updateRemainingBuffer(data: DataByteArray) {
        info { "Receive remaining buffer info" }
        if (data.size == 0) {
            info { "Data is empty, so empty invalidate buffer" }
            return
        }
        val bytes = data.value
        val remainingInternal = ByteBuffer.wrap(bytes).int
        info { "Invalidate buffer size. New size: $remainingInternal" }

        bufferSizeState.emit(remainingInternal)
    }

    private suspend fun CoroutineScope.sendCommandsWhileBufferNotEnd(
        bufferSize: Int
    ) {
        var remainingBufferSize = bufferSize

        while (isActive && remainingBufferSize > 0) {
            val pendingBytesToSend = getPendingBytesSafe(remainingBufferSize)

            remainingBufferSize -= pendingBytesToSend.size

            if (remainingBufferSize == 0) {
                info { "Sending only pending bytes" }
                serialApi.sendBytes(pendingBytesToSend)
                break
            }

            val (bytesToSend, pendingBytesInternal) = getPendingCommands(
                remainingBufferSize,
                waitInfiniteForFirstRequest = pendingBytesToSend.isEmpty()
            )
            check(remainingBufferSize >= bytesToSend.size) {
                "getPendingCommands can't return bytes (${bytesToSend.size}) " +
                    "more than buffer ($remainingBufferSize)"
            }
            remainingBufferSize -= bytesToSend.size
            pendingBytes = pendingBytesInternal

            serialApi.sendBytes(pendingBytesToSend + bytesToSend)
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun getPendingCommands(
        maxReadBytes: Int,
        waitInfiniteForFirstRequest: Boolean
    ): Pair<ByteArray, ByteArray?> {
        var remainBufferSize = maxReadBytes
        val byteStream = ByteArrayOutputStream()
        var pendingBytes: ByteArray? = null

        var bytesToSend = if (waitInfiniteForFirstRequest) {
            channel.receive()
        } else {
            withTimeoutOrNull(SERIAL_SEND_WAIT_TIMEOUT_MS) {
                channel.receive()
            }
        }

        while (remainBufferSize > 0 && bytesToSend != null) {
            if (remainBufferSize >= bytesToSend.size) {
                // Just send byte
                remainBufferSize -= bytesToSend.size
                byteStream.write(bytesToSend)
            } else if (remainBufferSize < bytesToSend.size) {
                // Send part of byte and put in pending bytes
                byteStream.write(bytesToSend.copyOf(remainBufferSize))
                pendingBytes = bytesToSend.copyOfRange(remainBufferSize, bytesToSend.size)
                remainBufferSize = 0 // here we end the while cycle
            }

            if (remainBufferSize > 0) {
                bytesToSend = withTimeoutOrNull(SERIAL_SEND_WAIT_TIMEOUT_MS) {
                    channel.receive()
                }
            }
        }

        return byteStream.toByteArray() to pendingBytes
    }

    private fun getPendingBytesSafe(maxLength: Int): ByteArray {
        val pendingBytesInternal = pendingBytes ?: return byteArrayOf()

        if (pendingBytesInternal.size <= maxLength) {
            pendingBytes = null
            return pendingBytesInternal
        }

        val toSend = pendingBytesInternal.copyOf(maxLength)
        pendingBytes = pendingBytesInternal.copyOfRange(maxLength, pendingBytesInternal.size)
        return toSend
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            serialApi: FSerialDeviceApi,
            scope: CoroutineScope,
            overflowCharacteristic: ClientBleGattCharacteristic,
            flipperActionNotifier: FlipperActionNotifier
        ): FSerialOverflowThrottler
    }
}
