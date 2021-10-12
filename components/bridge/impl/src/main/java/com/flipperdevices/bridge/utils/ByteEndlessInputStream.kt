package com.flipperdevices.bridge.utils

import it.unimi.dsi.fastutil.bytes.ByteArrayFIFOQueue
import java.io.InputStream

class ByteEndlessInputStream : InputStream() {
    private val queue = ByteArrayFIFOQueue()

    fun write(byteArray: ByteArray) = synchronized(queue) {
        byteArray.forEach { queue.enqueue(it) }
        queue.notifyAll()
    }

    override fun read(): Int = synchronized(queue) {
        // Wait while we not emit any bytes in queue
        while (queue.isEmpty) {
            queue.wait()
        }

        queue.dequeueByte().toInt()
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

