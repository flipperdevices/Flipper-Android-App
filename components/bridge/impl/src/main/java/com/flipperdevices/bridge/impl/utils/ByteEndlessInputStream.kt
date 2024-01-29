package com.flipperdevices.bridge.impl.utils

import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.verbose
import it.unimi.dsi.fastutil.bytes.ByteArrayFIFOQueue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import kotlin.coroutines.cancellation.CancellationException

class ByteEndlessInputStream(
    private val scope: CoroutineScope
) : InputStream(), LogTagProvider {
    override val TAG = "ByteEndlessInputStream"

    private val queue = ByteArrayFIFOQueue()
    private var balancer = 0

    init {
        scope.launch(Dispatchers.Default) {
            try {
                awaitCancellation()
            } finally {
                verbose { "Notify about cancel queue. Scope active is: ${scope.isActive}" }
                withContext(NonCancellable) {
                    synchronized(queue) {
                        queue.notifyAll()
                    }
                }
            }
        }
    }

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
            if (!scope.isActive) {
                throw CancellationException()
            }
            queue.wait()
        }
        if (!scope.isActive) {
            throw CancellationException()
        }

        return queue.dequeueByte()
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
