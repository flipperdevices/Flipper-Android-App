package com.flipperdevices.bridge.impl.manager.service

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import com.flipperdevices.bridge.api.manager.service.FlipperSerialApi
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.impl.manager.UnsafeBleManager
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import it.unimi.dsi.fastutil.bytes.ByteArrayFIFOQueue
import java.nio.ByteBuffer
import kotlin.math.min
import no.nordicsemi.android.ble.data.Data

class FlipperSerialOverflowThrottler(
    private val serialApi: FlipperSerialApi
) : FlipperSerialApi,
    BluetoothGattServiceWrapper,
    LogTagProvider {
    override val TAG = "FlipperSerialOverflowThrottler"
    private var overflowCharacteristics: BluetoothGattCharacteristic? = null

    private var remainBufferSize = 0
    private val outputBuffer = ByteArrayFIFOQueue()

    override fun receiveBytesFlow() = serialApi.receiveBytesFlow()

    override fun sendBytes(data: ByteArray) = synchronized(outputBuffer) {
        // Just put in pending bytes
        if (remainBufferSize == 0) {
            data.forEach { outputBuffer.enqueue(it) }
            return@synchronized
        }

        // Just send byte
        if (remainBufferSize > data.size) {
            remainBufferSize -= data.size
            info { "Send ${data.size}. Remain size $remainBufferSize" }
            serialApi.sendBytes(data)
            return@synchronized
        }

        // Send part of byte and put in pending bytes
        if (remainBufferSize < data.size) {
            info { "Send $remainBufferSize. Remain size $remainBufferSize" }
            serialApi.sendBytes(data.copyOf(remainBufferSize))
            val pending = data.copyOfRange(remainBufferSize, data.size)
            remainBufferSize = 0
            pending.forEach { outputBuffer.enqueue(it) }
            return@synchronized
        }
    }

    override fun onServiceReceived(service: BluetoothGattService) {
        overflowCharacteristics = service.getCharacteristic(Constants.BLESerialService.OVERFLOW)
    }

    override fun initialize(bleManager: UnsafeBleManager) {
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
        synchronized(outputBuffer) {
            remainBufferSize = 0
        }
        bleManager.readCharacteristicUnsafe(overflowCharacteristics).with { _, data ->
            updateRemainingBuffer(data)
        }.enqueue()
    }

    private fun updateRemainingBuffer(data: Data) {
        info { "Receive remaining buffer info" }
        val bytes = data.value ?: return
        val remainingInternal = ByteBuffer.wrap(bytes).int
        synchronized(outputBuffer) {
            remainBufferSize = remainingInternal
            info { "Invalidate buffer size. New size: $remainingInternal" }
            invalidate()
        }
    }

    /**
     * Send pending bytes
     */
    private fun invalidate() {
        if (outputBuffer.isEmpty) {
            info { "Output buffer empty" }
            return
        }

        val pendingSize = min(remainBufferSize, outputBuffer.size())
        val pendingBytes = ByteBuffer.allocate(pendingSize)
        repeat(pendingSize) {
            pendingBytes.put(outputBuffer.dequeueByte())
        }
        info { "Send $pendingSize. Remain size $remainBufferSize" }
        serialApi.sendBytes(pendingBytes.array())
        remainBufferSize -= pendingSize

        if (outputBuffer.isEmpty) {
            info { "Output buffer empty" }
        }
    }
}
