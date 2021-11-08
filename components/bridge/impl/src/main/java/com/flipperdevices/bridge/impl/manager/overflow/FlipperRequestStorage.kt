package com.flipperdevices.bridge.impl.manager.overflow

import com.flipperdevices.bridge.api.model.FlipperRequest
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.protobuf.toDelimitedBytes
import com.flipperdevices.core.log.LogTagProvider
import java.io.ByteArrayOutputStream

/**
 * Provider which store requests and provide it outside when it needed
 */
interface FlipperRequestStorage : LogTagProvider {
    /**
     * Store request internal and respect priority
     */
    fun sendRequest(vararg requests: FlipperRequest)

    /**
     * Call when call site ready to process next request
     * @return null if time is up
     */
    suspend fun getNextRequest(timeout: Long): FlipperRequest?
}

/**
 * @return pair where
 * first is bytes to send
 * second is pending bytes (can be null)
 */
@Suppress("BlockingMethodInNonBlockingContext")
suspend fun FlipperRequestStorage.getPendingCommands(
    maxReadBytes: Int,
    timeout: Long = Constants.BLE.RPC_SEND_WAIT_TIMEOUT_MS
): Pair<ByteArray, ByteArray?> {
    var remainBufferSize = maxReadBytes
    val byteStream = ByteArrayOutputStream()
    var pendingBytes: ByteArray? = null

    while (remainBufferSize > 0) {
        val request = getNextRequest(timeout) ?: break

        val bytesToSend = request.data.toDelimitedBytes()

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
    }

    return byteStream.toByteArray() to pendingBytes
}
