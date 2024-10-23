package com.flipperdevices.bridge.impl.manager.overflow

import com.flipperdevices.bridge.api.manager.service.FlipperSerialApi
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.protobuf.toDelimitedBytes
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.file
import com.flipperdevices.protobuf.storage.writeRequest
import com.flipperdevices.protobuf.system.pingRequest
import com.google.protobuf.ByteString
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import no.nordicsemi.android.ble.data.Data
import org.junit.Assert
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.mockito.stubbing.OngoingStubbing
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

@Ignore(
    """
    I can't fix in one day. More info about migration:
    https://github.com/Kotlin/kotlinx.coroutines/blob/master/kotlinx-coroutines-test/MIGRATION.md
    """
)
// TODO(LionZXY): https://flipperzero.atlassian.net/browse/MOB-75
@ExperimentalCoroutinesApi
class FlipperSerialOverflowThrottlerTest {
    private lateinit var coroutineScope: TestScope
    private lateinit var serialApi: FlipperSerialApi
    private lateinit var requestStorage: FlipperRequestStorage

    private lateinit var subject: FlipperSerialOverflowThrottler

    @Before
    fun setUp() {
        serialApi = mock()
        coroutineScope = TestScope()
        requestStorage = mock()
        subject = FlipperSerialOverflowThrottler(
            serialApi,
            coroutineScope,
            requestStorage
        )

        subject.onServiceReceived(mock())
    }

    @Test
    fun `Send request fitting in buffer size`() = runTest {
        val testRequest = main { systemPingRequest = pingRequest { } }.wrapToRequest()
        val bufferSize = testRequest.data.toDelimitedBytes().size

        whenever(requestStorage.getNextRequest(any())).doReturn(testRequest)

        subject.updateRemainingBuffer(Data(bufferSize.asBytes()))

        verify(serialApi).sendBytes(eq(testRequest.data.toDelimitedBytes()))
    }

    @Test
    fun `Send request if we fit in buffer`() = runTest {
        val testRequest = main {
            systemPingRequest = pingRequest { data = ByteString.EMPTY }
        }.wrapToRequest()
        val bufferSize = 400

        whenever(requestStorage.getNextRequest(any())).doReturnOnFirst(testRequest)
        whenever(serialApi.sendBytes(any())).doAnswer {
            coroutineScope.cancel()
        }

        subject.updateRemainingBuffer(Data(bufferSize.asBytes()))

        verify(serialApi).sendBytes(eq(testRequest.data.toDelimitedBytes()))
    }

    @Test
    fun `Send request larger than buffer`() = runTest {
        val testRequest = main {
            storageWriteRequest = writeRequest {
                file = file {
                    data = ByteString.copyFrom(
                        ByteArray(10)
                    )
                }
            }
        }.wrapToRequest()
        val bufferSize = 10
        check(testRequest.data.toDelimitedBytes().size > bufferSize)
        check(testRequest.data.toDelimitedBytes().size < bufferSize * 2)
        val actualBytes = ByteArrayOutputStream()
        val expectedBytes = testRequest.data.toDelimitedBytes()

        whenever(requestStorage.getNextRequest(any())).doReturnOnFirst(testRequest)
        var requestCount = 0
        whenever(serialApi.sendBytes(any())).doAnswer {
            actualBytes.write(it.getArgument(0, ByteArray::class.java))
            requestCount++
            if (requestCount > 1) {
                coroutineScope.cancel()
            }
        }

        subject.updateRemainingBuffer(Data(bufferSize.asBytes()))

        verify(serialApi).sendBytes(eq(expectedBytes.copyOf(bufferSize)))

        subject.updateRemainingBuffer(Data(bufferSize.asBytes()))

        Assert.assertArrayEquals(testRequest.data.toDelimitedBytes(), actualBytes.toByteArray())
    }
}

fun <T> OngoingStubbing<T>.doReturnOnFirst(
    answer: T
): OngoingStubbing<T> {
    var requestCount = 0
    return doAnswer {
        requestCount++
        if (requestCount > 1) {
            return@doAnswer null
        }
        return@doAnswer answer
    }
}

private fun Int.asBytes(): ByteArray {
    val byteBuffer = ByteBuffer.allocate(Int.SIZE_BYTES)
    byteBuffer.putInt(this)
    return byteBuffer.array()
}
