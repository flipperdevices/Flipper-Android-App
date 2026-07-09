package com.flipperdevices.bridge.connection.transport.usb.impl.model

import com.fazecast.jSerialComm.SerialPort
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.time.Duration.Companion.milliseconds

private const val OPEN_PORT_SAFETY_SLEEP_MS = 1000
private const val READ_BUFFER_SIZE = 4096
private val DTR_RESET_PULSE = 100.milliseconds

class USBDesktopDevice(
    private val serialPort: SerialPort,
    private val scope: CoroutineScope,
    private val receiveBuffer: USBReceiveBuffer
) : USBPlatformDevice, LogTagProvider {
    override val TAG = "USBDesktopDevice"

    private val closed = AtomicBoolean(false)

    private fun toDesktopStopBits(stopBits: USBSerialStopBits): Int = when (stopBits) {
        USBSerialStopBits.ONE -> SerialPort.ONE_STOP_BIT
        USBSerialStopBits.ONE_POINT_FIVE -> SerialPort.ONE_POINT_FIVE_STOP_BITS
        USBSerialStopBits.TWO -> SerialPort.TWO_STOP_BITS
    }

    private fun toDesktopParity(parity: USBSerialParity): Int = when (parity) {
        USBSerialParity.NONE -> SerialPort.NO_PARITY
        USBSerialParity.ODD -> SerialPort.ODD_PARITY
        USBSerialParity.EVEN -> SerialPort.EVEN_PARITY
        USBSerialParity.MARK -> SerialPort.MARK_PARITY
        USBSerialParity.SPACE -> SerialPort.SPACE_PARITY
    }

    private fun readIntoBufferBlocking() {
        val buffer = ByteArray(READ_BUFFER_SIZE)
        while (true) {
            val readCount = serialPort.readBytes(buffer, buffer.size)
            if (readCount < 0) {
                receiveBuffer.close(
                    USBPortClosedException("Serial port read failed with code $readCount")
                )
                return
            }
            if (readCount > 0) {
                receiveBuffer.append(buffer.copyOf(readCount))
            }
        }
    }

    private fun openPortBlocking(params: USBSerialPortParams) {
        serialPort.setComPortParameters(
            params.baudRate,
            params.dataBits,
            toDesktopStopBits(params.stopBits),
            toDesktopParity(params.parity)
        )
        if (!serialPort.openPort(OPEN_PORT_SAFETY_SLEEP_MS)) {
            throw USBPortOpenException("Failed to open serial port ${serialPort.systemPortName}")
        }
        serialPort.clearDTR()
        serialPort.clearRTS()
        // Reads block until at least one byte arrives and writes block until
        // the whole buffer is flushed, so `write` is all-or-nothing and the
        // read loop never spins on empty results.
        serialPort.setComPortTimeouts(
            SerialPort.TIMEOUT_READ_SEMI_BLOCKING or SerialPort.TIMEOUT_WRITE_BLOCKING,
            0,
            0
        )
    }

    /**
     * The port is opened with DTR/RTS de-asserted and the lines are raised
     * only after [DTR_RESET_PULSE]. The Flipper treats the DTR drop as a
     * terminal disconnect and resets its CLI session, so a session left in
     * RPC mode by a killed process cannot break the next handshake.
     */
    override suspend fun open(params: USBSerialPortParams): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching { openPortBlocking(params) }
                .mapCatching {
                    delay(DTR_RESET_PULSE)
                    if (!serialPort.setDTR() || !serialPort.setRTS()) {
                        throw USBPortOpenException(
                            "Failed to assert DTR/RTS on ${serialPort.systemPortName}"
                        )
                    }
                }
                .onSuccess {
                    scope.launch(Dispatchers.IO) { readIntoBufferBlocking() }
                }
        }

    override suspend fun write(data: ByteArray): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val writtenBytes = serialPort.writeBytes(data, data.size)
            if (writtenBytes != data.size) {
                throw USBPortClosedException(
                    "Serial port write returned $writtenBytes of ${data.size} bytes"
                )
            }
        }
    }

    override suspend fun read(): Result<ByteArray> = receiveBuffer.awaitNextChunk()

    override suspend fun close() {
        if (!closed.compareAndSet(false, true)) {
            return
        }
        withContext(NonCancellable + Dispatchers.IO) {
            info { "Closing serial port ${serialPort.systemPortName}" }
            serialPort.closePort()
            receiveBuffer.close(cause = null)
        }
    }
}
