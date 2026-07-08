package com.flipperdevices.bridge.connection.transport.usb.impl.model

import android.app.PendingIntent
import android.content.Intent
import android.hardware.usb.UsbManager
import com.flipperdevices.core.activityholder.CurrentActivityHolder
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.hoho.android.usbserial.driver.UsbSerialDriver
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.util.SerialInputOutputManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.job
import java.io.IOException

private const val RW_TIMEOUT = 0
private const val ACTION_USB_PERMISSION = "com.flipperdevices.bridge.connection.USB_PERMISSION"

class USBAndroidDevice(
    private val serialDriver: UsbSerialDriver,
    private val usbManager: UsbManager,
    private val scope: CoroutineScope
) : USBPlatformDevice, LogTagProvider {
    override val TAG = "USBAndroidDevice"

    private val serialPort = serialDriver.ports.first()
    private val serialListener = USBSerialListener(scope)
    private var ioManager: SerialInputOutputManager? = null

    private fun toAndroidStopBits(stopBits: Int): Int = when (stopBits) {
        USBPlatformDevice.ONE_STOP_BIT -> UsbSerialPort.STOPBITS_1
        USBPlatformDevice.ONE_POINT_FIVE_STOP_BITS -> UsbSerialPort.STOPBITS_1_5
        USBPlatformDevice.TWO_STOP_BITS -> UsbSerialPort.STOPBITS_2
        else -> error("Unknown stop bits value $stopBits")
    }

    private fun toAndroidParity(parity: Int): Int = when (parity) {
        USBPlatformDevice.NO_PARITY -> UsbSerialPort.PARITY_NONE
        USBPlatformDevice.ODD_PARITY -> UsbSerialPort.PARITY_ODD
        USBPlatformDevice.EVEN_PARITY -> UsbSerialPort.PARITY_EVEN
        USBPlatformDevice.MARK_PARITY -> UsbSerialPort.PARITY_MARK
        USBPlatformDevice.SPACE_PARITY -> UsbSerialPort.PARITY_SPACE
        else -> error("Unknown parity value $parity")
    }

    private fun requestPermission() {
        if (usbManager.hasPermission(serialDriver.device)) {
            return
        }
        val activity = CurrentActivityHolder.getCurrentActivity()
            ?: error("Failed get current activity")
        val intent = Intent(ACTION_USB_PERMISSION)
        intent.setPackage(activity.packageName)
        val usbPermissionIntent =
            PendingIntent.getBroadcast(activity, 0, intent, PendingIntent.FLAG_MUTABLE)
        usbManager.requestPermission(serialDriver.device, usbPermissionIntent)
    }

    override fun connect(baudRate: Int, dataBits: Int, stopBits: Int, parity: Int): Boolean {
        val connection = usbManager.openDevice(serialPort.device)
        if (connection == null) {
            requestPermission()
            error("Connection is null, request permission")
        }

        serialPort.open(connection)
        try {
            serialPort.setParameters(
                baudRate,
                dataBits,
                toAndroidStopBits(stopBits),
                toAndroidParity(parity)
            )
            serialPort.dtr = true
            serialPort.rts = true
        } catch (configurationError: IOException) {
            serialPort.close()
            throw configurationError
        }

        val manager = SerialInputOutputManager(serialPort, serialListener)
        ioManager = manager
        manager.start()
        scope.coroutineContext.job.invokeOnCompletion { manager.stop() }
        return true
    }

    override fun closePort() {
        ioManager?.stop()
        serialPort.close()
    }

    override fun writeBytes(buffer: ByteArray, bytesToWrite: Int, offset: Int): Int {
        val subBuffer = if (offset == 0) {
            buffer
        } else {
            buffer.copyOfRange(offset, offset + bytesToWrite)
        }
        return runCatching {
            serialPort.write(subBuffer, bytesToWrite, RW_TIMEOUT)
            bytesToWrite
        }.onFailure { writeError -> error(writeError) { "Fail write $bytesToWrite bytes" } }
            .getOrDefault(-1)
    }

    override fun readBytes(buffer: ByteArray, bytesToRead: Int): Int {
        return serialListener.readBytes(buffer, bytesToRead)
    }
}
