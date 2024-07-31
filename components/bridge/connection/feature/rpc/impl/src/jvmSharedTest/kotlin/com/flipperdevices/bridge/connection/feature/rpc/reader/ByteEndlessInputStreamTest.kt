package com.flipperdevices.bridge.connection.feature.rpc.reader

import com.flipperdevices.bridge.connection.pbutils.decodeDelimitedPackage
import com.flipperdevices.bridge.connection.pbutils.writeDelimitedTo
import com.flipperdevices.protobuf.Main
import com.flipperdevices.protobuf.storage.File
import com.flipperdevices.protobuf.storage.WriteRequest
import com.flipperdevices.protobuf.system.PingRequest
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import okio.ByteString.Companion.toByteString
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

class ByteEndlessInputStreamTest {
    lateinit var scope: TestScope
    lateinit var subject: ByteEndlessInputStream

    @Before
    fun setUp() {
        scope = TestScope()
        subject = ByteEndlessInputStream(scope)
        scope.advanceUntilIdle()
    }

    @Test
    fun `Input bytes equal output bytes`() {
        val testData = IntArray(size = 100) { it }
        val buffer = ByteBuffer.allocate(testData.size * Int.SIZE_BYTES)
        testData.forEach {
            buffer.putInt(it)
        }

        subject.write(buffer.array())

        val outputBuffer = ByteBuffer.allocate(testData.size * Int.SIZE_BYTES)
        repeat(testData.size) {
            outputBuffer.putInt(subject.read())
        }

        Assert.assertEquals(buffer, outputBuffer)
    }

    @Test
    fun `check ping request protobuf serialization`() {
        val message = Main(
            system_ping_request = PingRequest()
        )
        val messageBytes = ByteArrayOutputStream().use { os ->
            message.writeDelimitedTo(os)
            return@use os.toByteArray()
        }

        Assert.assertArrayEquals(
            byteArrayOf(2, 42, 0),
            messageBytes
        )
    }

    @Test
    fun `Write read protobuf message`() {
        val message = Main(
            system_ping_request = PingRequest()
        )
        val messageBytes = ByteArrayOutputStream().use { os ->
            message.writeDelimitedTo(os)
            return@use os.toByteArray()
        }

        subject.write(messageBytes)

        val actualMessage = Main.ADAPTER.decodeDelimitedPackage(subject)
        Assert.assertEquals(message, actualMessage)
    }

    @Test
    fun `Write read large protobuf message`() {
        val message = Main(
            storage_write_request = WriteRequest(
                file_ = File(
                    data_ = ByteArray(size = 10000) { it.toByte() }.toByteString()
                )
            ),
        )
        val messageBytes = ByteArrayOutputStream().use { os ->
            message.writeDelimitedTo(os)
            return@use os.toByteArray()
        }

        subject.write(messageBytes)

        val actualMessage = Main.ADAPTER.decodeDelimitedPackage(subject)
        Assert.assertEquals(message, actualMessage)
    }

    @Test
    fun `On cancel scope we close stream`() = runTest {
        var cancelException: Throwable? = null
        val deffered = async {
            cancelException = runCatching {
                Main.ADAPTER.decodeDelimitedPackage(subject)
            }.exceptionOrNull()
        }
        scope.cancel()
        scope.advanceUntilIdle()
        deffered.await()
        Assert.assertTrue(cancelException is CancellationException)
    }
}
