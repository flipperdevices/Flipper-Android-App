package com.flipperdevices.bridge.impl.manager.overflow

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import androidx.annotation.VisibleForTesting
import com.flipperdevices.bridge.api.manager.service.FlipperSerialApi
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.impl.manager.UnsafeBleManager
import com.flipperdevices.bridge.impl.manager.service.BluetoothGattServiceWrapper
import com.flipperdevices.bridge.impl.manager.service.getCharacteristicOrLog
import com.flipperdevices.bridge.impl.manager.service.getServiceOrLog
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.ktx.jre.withLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import no.nordicsemi.android.ble.data.Data
import java.nio.ByteBuffer

const val CLASS_TAG = "FlipperSerialOverflowThrottler"

class FlipperSerialOverflowThrottler(
    private val serialApi: FlipperSerialApi,
    private val scope: CoroutineScope,
    private val requestStorage: FlipperRequestStorage
) : BluetoothGattServiceWrapper,
    LogTagProvider {
    override val TAG = CLASS_TAG

    private var overflowCharacteristics: BluetoothGattCharacteristic? = null

    private val mutex = Mutex()
    private var pendingBytes: ByteArray? = null
    private var overflowBufferJob: Job? = null

    /**
     * Bytes waiting to be sent to the device
     */
    private val bufferSizeState = MutableSharedFlow<Int>(replay = 1)

    override fun onServiceReceived(gatt: BluetoothGatt): Boolean {
        val service = getServiceOrLog(gatt, Constants.BLESerialService.SERVICE_UUID) ?: return false
        overflowCharacteristics = getCharacteristicOrLog(
            service,
            Constants.BLESerialService.OVERFLOW
        ) ?: return false
        return true
    }

    override suspend fun initialize(
        bleManager: UnsafeBleManager
    ) = withLock(mutex, "initialize") {
        overflowBufferJob?.cancelAndJoin()
        overflowBufferJob = getOverflowBufferJob()
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

    override suspend fun reset(
        bleManager: UnsafeBleManager
    ) = withLock(mutex, "reset") {
        overflowBufferJob?.cancelAndJoin()
        overflowBufferJob = null
        info { "Cancel overflow buffer job" }
        bufferSizeState.emit(0)
        pendingBytes = null
    }

    @VisibleForTesting
    fun updateRemainingBuffer(data: Data) {
        info { "Receive remaining buffer info" }
        val bytes = data.value ?: return
        val remainingInternal = ByteBuffer.wrap(bytes).int
        info { "Invalidate buffer size. New size: $remainingInternal" }

        scope.launch(FlipperDispatchers.workStealingDispatcher) {
            bufferSizeState.emit(remainingInternal)
        }
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

    @Suppress("MagicNumber")
    fun sendTrashBytesAndBrokeSession() {
        val randomBytes = byteArrayOf(-61, 91, 69, 107, -128, -69, -42, 107, 53, -102)
        serialApi.sendBytes(randomBytes)
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

    private fun getOverflowBufferJob(): Job {
        return scope.launch(FlipperDispatchers.workStealingDispatcher) {
            bufferSizeState.collectLatest { bufferSize ->
                sendCommandsWhileBufferNotEnd(bufferSize)
            }
        }
    }
}
