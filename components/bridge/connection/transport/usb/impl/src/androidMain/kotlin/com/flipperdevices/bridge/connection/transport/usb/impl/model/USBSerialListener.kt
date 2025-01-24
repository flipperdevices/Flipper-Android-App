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

    init {
        scope.launch {
            try {
                awaitCancellation()
            } finally {
                withContext(NonCancellable) {
                    synchronized(queue) {
                        queue.notifyAll()
                    }
                }
            }
        }
    }

    fun readBytes(
        buffer: ByteArray,
        bytesToRead: Int
    ): Int {
        var index = 0
        synchronized(queue) {
            while (queue.isEmpty) {
                queue.wait()
            }
            while (index < bytesToRead && !queue.isEmpty) {
                buffer[index++] = queue.dequeueByte()
            }
        }
        return index
    }

    override fun onNewData(data: ByteArray?) {
        info { "Receive $data" }
        synchronized(queue) {
            data?.forEach {
                queue.enqueue(it)
            }
            queue.notifyAll()
        }
    }

    override fun onRunError(e: Exception?) {
        error(e) { "Failed in usb serial" }
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
