package com.flipperdevices.bridge.connection.transport.usb.impl.model

import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.hoho.android.usbserial.util.SerialInputOutputManager

class USBSerialListener(
    private val receiveBuffer: USBReceiveBuffer
) : SerialInputOutputManager.Listener, LogTagProvider {
    override val TAG = "USBSerialListener"

    override fun onNewData(data: ByteArray?) {
        if (data == null || data.isEmpty()) {
            return
        }
        // SerialInputOutputManager allocates a fresh array per callback,
        // so the chunk can be handed over without a defensive copy.
        receiveBuffer.append(data)
    }

    override fun onRunError(e: Exception?) {
        error(e) { "USB serial I/O thread failed" }
        receiveBuffer.close(e)
    }
}
