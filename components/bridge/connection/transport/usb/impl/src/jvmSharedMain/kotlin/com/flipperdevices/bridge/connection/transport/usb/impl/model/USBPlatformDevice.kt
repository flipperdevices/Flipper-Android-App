package com.flipperdevices.bridge.connection.transport.usb.impl.model

/**
 * Platform-specific serial port for a single USB connection session.
 *
 * Instances are single-use: [open] once, then [close] once.
 * [close] is idempotent and safe to call concurrently from any state.
 */
interface USBPlatformDevice {
    /**
     * Opens the port with [params] and starts collecting incoming bytes
     * into an internal buffer consumed via [read].
     */
    suspend fun open(params: USBSerialPortParams): Result<Unit>

    /**
     * Writes [data] fully or returns a failure. Partial writes never succeed.
     */
    suspend fun write(data: ByteArray): Result<Unit>

    /**
     * Suspends until the next chunk of incoming bytes is available.
     * Returns [USBPortClosedException] failure when the port is closed.
     */
    suspend fun read(): Result<ByteArray>

    suspend fun close()
}
