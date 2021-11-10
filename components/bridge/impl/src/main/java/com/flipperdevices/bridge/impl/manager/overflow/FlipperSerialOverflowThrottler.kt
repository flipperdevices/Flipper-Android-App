package com.flipperdevices.bridge.impl.manager.overflow

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import androidx.annotation.VisibleForTesting
import com.flipperdevices.bridge.api.manager.service.FlipperSerialApi
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.impl.manager.UnsafeBleManager
import com.flipperdevices.bridge.impl.manager.service.BluetoothGattServiceWrapper
import com.flipperdevices.core.ktx.newSingleThreadExecutor
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import java.nio.ByteBuffer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import no.nordicsemi.android.ble.data.Data

const val CLASS_TAG = "FlipperSerialOverflowThrottler"

class FlipperSerialOverflowThrottler(
    private val serialApi: FlipperSerialApi,
    private val scope: CoroutineScope,
    private val requestStorage: FlipperRequestStorage,
    private val dispatcher: CoroutineDispatcher = newSingleThreadExecutor(CLASS_TAG)
        .asCoroutineDispatcher()
) : BluetoothGattServiceWrapper,
    LogTagProvider {
    override val TAG = CLASS_TAG
    private var overflowCharacteristics: BluetoothGattCharacteristic? = null

    private var pendingBytes: ByteArray? = null

    /**
     * Bytes waiting to be sent to the device
     */
    private var bufferSizeState = MutableSharedFlow<Int>(replay = 1)

    init {
        scope.launch {
            withContext(dispatcher) {
                bufferSizeState.collect { bufferSize ->
                    sendCommandsWhileBufferNotEnd(bufferSize)
                }
            }
        }
    }

    override fun onServiceReceived(service: BluetoothGattService) {
        overflowCharacteristics = service.getCharacteristic(Constants.BLESerialService.OVERFLOW)
    }

    override fun initialize(bleManager: UnsafeBleManager) {
        pendingBytes = null
        bleManager.setNotificationCallbackUnsafe(overflowCharacteristics).with { _, data ->
            updateRemainingBuffer(data)
        }
        bleManager.enableNotificationsUnsafe(overflowCharacteristics).enqueue()
        bleManager.enableIndicationsUnsafe(overflowCharacteristics).enqueue()
        bleManager.readCharacteristicUnsafe(overflowCharacteristics).with { _, data ->
            updateRemainingBuffer(data)
        }.enqueue()
    }

    override fun reset(bleManager: UnsafeBleManager) {
        pendingBytes = null
        bleManager.readCharacteristicUnsafe(overflowCharacteristics).with { _, data ->
            updateRemainingBuffer(data)
        }.enqueue()
    }

    @VisibleForTesting
    fun updateRemainingBuffer(data: Data) {
        info { "Receive remaining buffer info" }
        val bytes = data.value ?: return
        val remainingInternal = ByteBuffer.wrap(bytes).int
        info { "Invalidate buffer size. New size: $remainingInternal" }

        scope.launch {
            bufferSizeState.emit(remainingInternal)
        }
    }

    @Synchronized
    private suspend fun CoroutineScope.sendCommandsWhileBufferNotEnd(bufferSize: Int) {
        var remainingBufferSize = bufferSize

        while (isActive && remainingBufferSize > 0) {
            val pendingBytesToSend = getPendingBytesSafe(remainingBufferSize)

            remainingBufferSize -= pendingBytesToSend.size

            if (remainingBufferSize == 0) {
                info { "Sending only pending bytes" }
                serialApi.sendBytes(pendingBytesToSend)
                break
            }

            val (bytesToSend, pendingBytesInternal) = requestStorage.getPendingCommands(
                remainingBufferSize,
                Constants.BLE.RPC_SEND_WAIT_TIMEOUT_MS
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
}
