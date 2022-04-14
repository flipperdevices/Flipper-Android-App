package com.flipperdevices.bridge.impl.utils

import it.unimi.dsi.fastutil.bytes.ByteArrayFIFOQueue
import java.io.InputStream
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.cancellation.CancellationException

class ByteEndlessInputStream : InputStream() {
    private val isRunning = AtomicBoolean(true)

    private val queue = ByteArrayFIFOQueue()
    private var balancer = 0

    fun write(byteArray: ByteArray) = synchronized(queue) {
        byteArray.forEach {
            balancer++
            queue.enqueue(it)
        }
        queue.notifyAll()
    }

    override fun read(): Int = synchronized(queue) {
        /**
         * We convert to UByte because we want response data as in ByteArrayInputStream
         * So normally byte can be from -128 to 127
         * But read method from input stream should answer from 0 to 255
         * So we convert it in ubyte, and then convert to int
         * {@see ByteArrayInputStream#read}
         */
        return@synchronized readOneByte().toUByte().toInt()
    }

    private fun readOneByte(): Byte {
        // Wait while we not emit any bytes in queue
        while (queue.isEmpty) {
            if (!isRunning.get()) {
                throw CancellationException()
            }
            queue.wait()
        }

        return queue.dequeueByte()
    }

    fun stop() {
        isRunning.compareAndSet(true, false)
        queue.notifyAll()
    }
}

@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
private fun Any.wait() {
    (this as Object).wait()
}

@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
private fun Any.notifyAll() {
    (this as Object).notifyAll()
}
