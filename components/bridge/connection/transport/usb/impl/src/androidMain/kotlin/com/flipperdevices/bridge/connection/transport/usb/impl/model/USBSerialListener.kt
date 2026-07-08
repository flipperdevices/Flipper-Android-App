package com.flipperdevices.bridge.connection.transport.usb.impl.model

import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.hoho.android.usbserial.util.SerialInputOutputManager
import it.unimi.dsi.fastutil.bytes.ByteArrayFIFOQueue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class USBSerialListener(
    scope: CoroutineScope
) : SerialInputOutputManager.Listener, LogTagProvider {
    override val TAG = "USBSerialListener"

    private val queue = ByteArrayFIFOQueue()
    private var closed = false

    init {
        scope.launch {
            try {
                awaitCancellation()
            } finally {
                withContext(NonCancellable) {
                    markClosed()
                }
            }
        }
    }

    private fun markClosed() {
        synchronized(queue) {
            closed = true
            queue.notifyAll()
        }
    }

    fun readBytes(
        buffer: ByteArray,
        bytesToRead: Int
    ): Int {
        var index = 0
        synchronized(queue) {
            while (queue.isEmpty) {
                if (closed) {
                    return -1
                }
                queue.wait()
            }
            while (index < bytesToRead && !queue.isEmpty) {
                buffer[index++] = queue.dequeueByte()
            }
        }
        return index
    }

    override fun onNewData(data: ByteArray?) {
        info { "Receive ${data?.size ?: 0} bytes" }
        synchronized(queue) {
            data?.forEach { receivedByte ->
                queue.enqueue(receivedByte)
            }
            queue.notifyAll()
        }
    }

    override fun onRunError(e: Exception?) {
        error(e) { "Failed in usb serial" }
        markClosed()
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
