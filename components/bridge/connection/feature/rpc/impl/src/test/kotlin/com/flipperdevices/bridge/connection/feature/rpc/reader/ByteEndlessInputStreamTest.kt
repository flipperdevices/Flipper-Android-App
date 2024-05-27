package com.flipperdevices.bridge.connection.feature.rpc.reader

import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.file
import com.flipperdevices.protobuf.storage.writeRequest
import com.flipperdevices.protobuf.system.pingRequest
import com.google.protobuf.ByteString
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
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
    fun `Write read protobuf message`() {
        val message = main {
            systemPingRequest = pingRequest {}
        }
        val messageBytes = ByteArrayOutputStream().use { os ->
            message.writeDelimitedTo(os)
            return@use os.toByteArray()
        }

        subject.write(messageBytes)

        val actualMessage = Flipper.Main.parseDelimitedFrom(subject)
        Assert.assertEquals(message, actualMessage)
    }

    @Test
    fun `Write read large protobuf message`() {
        val message = main {
            storageWriteRequest = writeRequest {
                file = file {
                    data = ByteString.copyFrom(ByteArray(size = 10000) { it.toByte() })
                }
            }
        }
        val messageBytes = ByteArrayOutputStream().use { os ->
            message.writeDelimitedTo(os)
            return@use os.toByteArray()
        }

        subject.write(messageBytes)

        val actualMessage = Flipper.Main.parseDelimitedFrom(subject)
        Assert.assertEquals(message, actualMessage)
    }

    @Test
    fun `On cancel scope we close stream`() = runTest {
        var cancelException: Throwable? = null
        val deffered = async {
            cancelException = runCatching {
                Flipper.Main.parseDelimitedFrom(subject)
            }.exceptionOrNull()
        }
        scope.cancel()
        scope.advanceUntilIdle()
        deffered.await()
        Assert.assertTrue(cancelException is CancellationException)
    }
}
