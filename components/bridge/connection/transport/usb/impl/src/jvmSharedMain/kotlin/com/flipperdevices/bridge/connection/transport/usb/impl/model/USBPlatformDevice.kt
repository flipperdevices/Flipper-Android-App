package com.flipperdevices.bridge.connection.transport.usb.impl.model

import com.flipperdevices.bridge.connection.transport.usb.api.FUSBDeviceConnectionConfig

interface USBPlatformDevice {
    fun connect(baudRate: Int, dataBits: Int, stopBits: Int, parity: Int): Boolean
    fun closePort()

    fun writeBytes(buffer: ByteArray, bytesToWrite: Int = buffer.size, offset: Int = 0): Int

    fun readBytes(buffer: ByteArray, bytesToRead: Int = buffer.size): Int

    companion object {
        // Parity Values
        const val NO_PARITY: Int = 0
        const val ODD_PARITY: Int = 1
        const val EVEN_PARITY: Int = 2
        const val MARK_PARITY: Int = 3
        const val SPACE_PARITY: Int = 4

        // Number of Stop Bits
        const val ONE_STOP_BIT: Int = 1
        const val ONE_POINT_FIVE_STOP_BITS: Int = 2
        const val TWO_STOP_BITS: Int = 3
    }
}