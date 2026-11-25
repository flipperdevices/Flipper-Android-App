package com.flipperdevices.bridge.connection.transport.usb.impl.model

import android.hardware.usb.UsbManager
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.hoho.android.usbserial.driver.UsbSerialDriver
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.util.SerialInputOutputManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

private val WRITE_TIMEOUT = 5.seconds
private val DTR_RESET_PULSE = 100.milliseconds

class USBAndroidDevice(
    private val serialDriver: UsbSerialDriver,
    private val usbManager: UsbManager,
    private val permissionRequester: USBPermissionRequester,
    private val receiveBuffer: USBReceiveBuffer,
    private val serialListener: USBSerialListener
) : USBPlatformDevice, LogTagProvider {
    override val TAG = "USBAndroidDevice"

    private val serialPort = serialDriver.ports.first()
    private val closed = AtomicBoolean(false)
    private var ioManager: SerialInputOutputManager? = null

    private fun toAndroidStopBits(stopBits: USBSerialStopBits): Int = when (stopBits) {
        USBSerialStopBits.ONE -> UsbSerialPort.STOPBITS_1
        USBSerialStopBits.ONE_POINT_FIVE -> UsbSerialPort.STOPBITS_1_5
        USBSerialStopBits.TWO -> UsbSerialPort.STOPBITS_2
    }

    private fun toAndroidParity(parity: USBSerialParity): Int = when (parity) {
        USBSerialParity.NONE -> UsbSerialPort.PARITY_NONE
        USBSerialParity.ODD -> UsbSerialPort.PARITY_ODD
        USBSerialParity.EVEN -> UsbSerialPort.PARITY_EVEN
        USBSerialParity.MARK -> UsbSerialPort.PARITY_MARK
        USBSerialParity.SPACE -> UsbSerialPort.PARITY_SPACE
    }

    private fun configureAndStartIo(params: USBSerialPortParams) {
        serialPort.setParameters(
            params.baudRate,
            params.dataBits,
            toAndroidStopBits(params.stopBits),
            toAndroidParity(params.parity)
        )
        serialPort.dtr = false
        serialPort.rts = false
        val manager = SerialInputOutputManager(serialPort, serialListener)
        ioManager = manager
        manager.start()
    }

    private fun openPortBlocking(params: USBSerialPortParams) {
        val connection = usbManager.openDevice(serialDriver.device)
            ?: throw USBPortOpenException(
                "System denied access to USB device ${serialDriver.device.deviceName}"
            )
        serialPort.open(connection)
        try {
            configureAndStartIo(params)
        } catch (configurationError: IOException) {
            serialPort.close()
            throw configurationError
        }
    }

    /**
     * The port is opened with DTR/RTS de-asserted and the lines are raised
     * only after [DTR_RESET_PULSE]. The Flipper treats the DTR drop as a
     * terminal disconnect and resets its CLI session, so a session left in
     * RPC mode by a killed process cannot break the next handshake.
     */
    override suspend fun open(params: USBSerialPortParams): Result<Unit> {
        permissionRequester.ensurePermission(serialDriver.device)
            .getOrElse { permissionError -> return Result.failure(permissionError) }
        return withContext(Dispatchers.IO) {
            runCatching { openPortBlocking(params) }
                .mapCatching {
                    delay(DTR_RESET_PULSE)
                    serialPort.dtr = true
                    serialPort.rts = true
                }
        }
    }

    override suspend fun write(data: ByteArray): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            serialPort.write(data, WRITE_TIMEOUT.inWholeMilliseconds.toInt())
        }.recoverCatching { writeError ->
            throw USBPortClosedException("Failed to write ${data.size} bytes", writeError)
        }
    }

    override suspend fun read(): Result<ByteArray> = receiveBuffer.awaitNextChunk()

    override suspend fun close() {
        if (!closed.compareAndSet(false, true)) {
            return
        }
        withContext(NonCancellable + Dispatchers.IO) {
            ioManager?.stop()
            receiveBuffer.close(cause = null)
            // De-assert control lines so the Flipper detects the terminal
            // disconnect and resets its CLI session on the next open.
            // Desktop's jSerialComm does this implicitly on close.
            runCatching {
                serialPort.dtr = false
                serialPort.rts = false
            }
            runCatching { serialPort.close() }
                .onFailure { closeError -> info { "Port close skipped: $closeError" } }
        }
    }
}
