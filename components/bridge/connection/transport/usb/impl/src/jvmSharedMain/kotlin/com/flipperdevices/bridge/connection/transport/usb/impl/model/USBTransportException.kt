package com.flipperdevices.bridge.connection.transport.usb.impl.model

sealed class USBTransportException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)

class USBDeviceNotFoundException(
    message: String,
    cause: Throwable? = null
) : USBTransportException(message, cause)

class USBDeviceAccessDeniedException(
    message: String
) : USBTransportException(message)

class USBPortOpenException(
    message: String,
    cause: Throwable? = null
) : USBTransportException(message, cause)

class USBPortClosedException(
    message: String,
    cause: Throwable? = null
) : USBTransportException(message, cause)

class USBHandshakeTimeoutException(
    message: String
) : USBTransportException(message)
