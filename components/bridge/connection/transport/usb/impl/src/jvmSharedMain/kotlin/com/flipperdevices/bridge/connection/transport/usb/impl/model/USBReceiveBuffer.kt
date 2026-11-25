package com.flipperdevices.bridge.connection.transport.usb.impl.model

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel

/**
 * Buffer between a platform serial reader (callback or blocking thread)
 * and coroutine consumers.
 *
 * The buffer is unbounded: serial throughput is tiny compared to consumer
 * speed, so backpressure would only add failure modes without real benefit.
 */
class USBReceiveBuffer {
    private val incomingChunks = Channel<ByteArray>(Channel.UNLIMITED)

    fun append(chunk: ByteArray) {
        incomingChunks.trySend(chunk)
    }

    fun close(cause: Throwable?) {
        incomingChunks.close(cause)
    }

    /**
     * Cancellation is rethrown so a cancelled consumer is never confused
     * with a closed port.
     */
    @Suppress("TooGenericExceptionCaught")
    suspend fun awaitNextChunk(): Result<ByteArray> {
        return try {
            Result.success(incomingChunks.receive())
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (portClosed: Exception) {
            Result.failure(USBPortClosedException("USB port is closed", portClosed))
        }
    }
}
