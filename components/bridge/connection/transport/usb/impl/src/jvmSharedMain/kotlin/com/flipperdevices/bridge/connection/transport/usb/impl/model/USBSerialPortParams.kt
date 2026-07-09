package com.flipperdevices.bridge.connection.transport.usb.impl.model

data class USBSerialPortParams(
    val baudRate: Int,
    val dataBits: Int,
    val stopBits: USBSerialStopBits,
    val parity: USBSerialParity
)
