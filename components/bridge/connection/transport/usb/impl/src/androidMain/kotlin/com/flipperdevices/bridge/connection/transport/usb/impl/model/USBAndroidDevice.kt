package com.flipperdevices.bridge.connection.transport.usb.impl.model

import android.app.PendingIntent
import android.content.Intent
import android.hardware.usb.UsbManager
import android.util.Log
import com.flipperdevices.core.activityholder.CurrentActivityHolder
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.hoho.android.usbserial.driver.UsbSerialDriver
import com.hoho.android.usbserial.util.SerialInputOutputManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val RW_TIMEOUT = 0

class USBAndroidDevice(
    private val serialDriver: UsbSerialDriver,
    private val usbManager: UsbManager,
    private val scope: CoroutineScope
) : USBPlatformDevice, LogTagProvider {
    override val TAG = "USBAndroidDevice"

    private val serialPort = serialDriver.ports.first()
    private val serialListener = USBSerialListener(scope)

    override fun connect(baudRate: Int, dataBits: Int, stopBits: Int, parity: Int): Boolean {
        val connection = runCatching { usbManager.openDevice(serialPort.device) }
            .onFailure { error(it) { "Fail open connection" } }
            .getOrNull()
        if (connection == null) {
            requestPermission()
            error("Connection is null, request permission")
        }

        serialPort.open(connection)
        serialPort.setParameters(baudRate, dataBits, stopBits, parity)
        serialPort.dtr = true
        serialPort.rts = true

        val ioManager = SerialInputOutputManager(serialPort, serialListener)
        scope.launch {
            try {
                awaitCancellation()
            } finally {
                withContext(NonCancellable) {
                    ioManager.stop()
                }
            }
        }
        ioManager.start()
        return true
    }

    private fun requestPermission() {
        if (!usbManager.hasPermission(serialDriver.device)) {

            val intent = Intent("Test")

            val activity = CurrentActivityHolder.getCurrentActivity()
                ?: error("Failed get current activity")
            intent.setPackage(activity.packageName)
            val usbPermissionIntent =
                PendingIntent.getBroadcast(activity, 0, intent, PendingIntent.FLAG_MUTABLE)
            usbManager.requestPermission(serialDriver.device, usbPermissionIntent)
            return
        }
    }

    override fun closePort() {
        serialPort.close()
    }

    override fun writeBytes(buffer: ByteArray, bytesToWrite: Int, offset: Int): Int {
        val subBuffer = if (offset == 0) {
            buffer
        } else {
            buffer.copyOfRange(offset, buffer.size)
        }
        serialPort.write(subBuffer, bytesToWrite, RW_TIMEOUT)
        return bytesToWrite
    }

    override fun readBytes(buffer: ByteArray, bytesToRead: Int): Int {
        return serialListener.readBytes(buffer, bytesToRead)
    }
}
