package com.flipperdevices.bridge.connection.transport.usb.impl.model

import com.fazecast.jSerialComm.SerialPort
import com.flipperdevices.bridge.connection.transport.usb.api.FUSBDeviceConnectionConfig

private const val OPEN_PORT_TIME_MS = 1000

class USBDesktopDevice(
    private val serialPort: SerialPort
) : USBPlatformDevice {
    override fun connect(baudRate: Int, dataBits: Int, stopBits: Int, parity: Int): Boolean {
        serialPort.setComPortParameters(
            baudRate,
            dataBits,
            SerialPort.ONE_STOP_BIT,
            SerialPort.NO_PARITY
        )
        serialPort.openPort(OPEN_PORT_TIME_MS)
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0)
        return serialPort.openPort()
    }

    override fun closePort() {
        serialPort.closePort()
    }

    override fun writeBytes(buffer: ByteArray, bytesToWrite: Int, offset: Int): Int {
        return serialPort.writeBytes(buffer, bytesToWrite, offset)
    }

    override fun readBytes(buffer: ByteArray, bytesToRead: Int): Int {
        return serialPort.readBytes(buffer, bytesToRead)
    }
}
