package com.flipperdevices.bridge.connection.ble.impl.serial

import com.flipperdevices.bridge.connection.common.api.serial.FSerialDeviceApi
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import no.nordicsemi.android.common.core.DataByteArray
import no.nordicsemi.android.kotlin.ble.client.main.service.ClientBleGattCharacteristic
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.nio.ByteBuffer

class FSerialOverflowThrottlerTest {
    private lateinit var overflowByteArrayFlow: MutableSharedFlow<DataByteArray>
    private lateinit var overflowCharacteristic: ClientBleGattCharacteristic
    private lateinit var serialApi: FSerialDeviceApi

    @Before
    fun setUp() {
        serialApi = mockk(relaxUnitFun = true)
        overflowByteArrayFlow = MutableSharedFlow(replay = 1)
        overflowCharacteristic = mockk(relaxUnitFun = true) {
            coEvery { getNotifications(any(), any()) } returns overflowByteArrayFlow
        }
    }

    @Test
    fun `send request if we fit in buffer`() = runTest {
        val childScope = TestScope(this.testScheduler)
        val underTest = FSerialOverflowThrottler(
            scope = childScope,
            serialApi = serialApi,
            overflowCharacteristic = overflowCharacteristic
        )
        val byteBuffer = ByteBuffer.wrap(ByteArray(4))
        byteBuffer.putInt(1024)
        overflowByteArrayFlow.emit(DataByteArray(byteBuffer.array()))

        underTest.sendBytes("TEST".toByteArray())

        childScope.advanceUntilIdle()
        childScope.advanceTimeBy(100L) // Timeout for reading buffer
        childScope.advanceUntilIdle()

        coVerify { serialApi.sendBytes(eq("TEST".toByteArray())) }

        childScope.cancel()
    }

    @Test
    fun `wait until buffer value is empty`() = runTest {
        val childScope = TestScope(this.testScheduler)
        val underTest = FSerialOverflowThrottler(
            scope = childScope,
            serialApi = serialApi,
            overflowCharacteristic = overflowCharacteristic
        )

        val result = runCatching {
            withTimeout(1000L) {
                underTest.sendBytes(byteArrayOf())
            }
        }

        childScope.advanceUntilIdle()
        childScope.advanceTimeBy(10000L) // Timeout for reading buffer and timeout waiting
        childScope.advanceUntilIdle()

        coVerify(inverse = true) { serialApi.sendBytes(any()) }

        Assert.assertTrue(result.exceptionOrNull() is TimeoutCancellationException)

        childScope.cancel()
    }

    @Test
    fun `wait when buffer is full`() = runTest {
        val childScope = TestScope(this.testScheduler)
        val underTest = FSerialOverflowThrottler(
            scope = childScope,
            serialApi = serialApi,
            overflowCharacteristic = overflowCharacteristic
        )
        val byteBuffer = ByteBuffer.wrap(ByteArray(4))
        byteBuffer.putInt(1024)
        overflowByteArrayFlow.emit(DataByteArray(byteBuffer.array()))
        val testBuffer = ByteArray(1024)
        underTest.sendBytes(testBuffer)

        childScope.advanceUntilIdle()
        childScope.advanceTimeBy(100L) // Timeout for reading buffer
        childScope.advanceUntilIdle()

        val result = runCatching {
            withTimeout(1000L) {
                underTest.sendBytes("TEST".toByteArray())
            }
        }

        childScope.advanceUntilIdle()
        childScope.advanceTimeBy(10000L) // Timeout for reading buffer and timeout waiting
        childScope.advanceUntilIdle()

        coVerify { serialApi.sendBytes(eq(testBuffer)) }
        coVerify(exactly = 1) { serialApi.sendBytes(any()) }

        Assert.assertTrue(result.exceptionOrNull() is TimeoutCancellationException)

        childScope.cancel()
    }

    @Test
    fun `send request fitting in buffer size`() = runTest {
        val childScope = TestScope(this.testScheduler)
        val underTest = FSerialOverflowThrottler(
            scope = childScope,
            serialApi = serialApi,
            overflowCharacteristic = overflowCharacteristic
        )
        val byteBuffer = ByteBuffer.wrap(ByteArray(4))
        byteBuffer.putInt(1024)
        overflowByteArrayFlow.emit(DataByteArray(byteBuffer.array()))
        val testBuffer = ByteArray(1024)
        underTest.sendBytes(testBuffer)

        childScope.advanceUntilIdle()
        childScope.advanceTimeBy(100L) // Timeout for reading buffer
        childScope.advanceUntilIdle()

        coVerify { serialApi.sendBytes(eq(testBuffer)) }

        childScope.cancel()
    }

    @Test
    fun `send request larger than buffer`() = runTest {
        val childScope = TestScope(this.testScheduler)
        val underTest = FSerialOverflowThrottler(
            scope = childScope,
            serialApi = serialApi,
            overflowCharacteristic = overflowCharacteristic
        )
        val byteBuffer = ByteBuffer.wrap(ByteArray(4))
        byteBuffer.putInt(1024)
        overflowByteArrayFlow.emit(DataByteArray(byteBuffer.array()))
        val testBuffer = ByteArray(1500, init = { it.toByte() })
        underTest.sendBytes(testBuffer)

        childScope.advanceUntilIdle()
        childScope.advanceTimeBy(100L) // Timeout for reading buffer
        childScope.advanceUntilIdle()

        overflowByteArrayFlow.emit(DataByteArray(byteBuffer.array()))

        childScope.advanceUntilIdle()
        childScope.advanceTimeBy(100L) // Timeout for reading buffer
        childScope.advanceUntilIdle()

        val firstArray = testBuffer.copyOf(1024)
        val secondArray = testBuffer.copyOfRange(1024, testBuffer.size)
        val combineArray = firstArray + secondArray
        Assert.assertEquals(testBuffer.toList(), combineArray.toList())

        coVerify { serialApi.sendBytes(eq(firstArray)) }
        coVerify { serialApi.sendBytes(eq(secondArray)) }

        childScope.cancel()
    }

    @Test
    fun `send two requests with pending bytes`() = runTest {
        val childScope = TestScope(this.testScheduler)
        val underTest = FSerialOverflowThrottler(
            scope = childScope,
            serialApi = serialApi,
            overflowCharacteristic = overflowCharacteristic
        )
        val byteBuffer = ByteBuffer.wrap(ByteArray(4))
        byteBuffer.putInt(1024)
        overflowByteArrayFlow.emit(DataByteArray(byteBuffer.array()))
        val testBuffer = ByteArray(1500, init = { it.toByte() })
        underTest.sendBytes(testBuffer)

        childScope.advanceUntilIdle()
        childScope.advanceTimeBy(100L) // Timeout for reading buffer
        childScope.advanceUntilIdle()

        val secondPacket = ByteArray(300, init = { it.toByte() })
        childScope.launch {
            underTest.sendBytes(secondPacket)
        }
        childScope.advanceUntilIdle()
        overflowByteArrayFlow.emit(DataByteArray(byteBuffer.array()))

        childScope.advanceUntilIdle()
        childScope.advanceTimeBy(100L) // Timeout for reading buffer
        childScope.advanceUntilIdle()

        coVerify { serialApi.sendBytes(eq(testBuffer.copyOf(1024))) }
        coVerify {
            serialApi.sendBytes(
                eq(
                    testBuffer.copyOfRange(
                        1024,
                        testBuffer.size
                    ) + secondPacket
                )
            )
        }

        childScope.cancel()
    }

    @Test
    fun `send request larger than two buffer`() = runTest {
        val childScope = TestScope(this.testScheduler)
        val underTest = FSerialOverflowThrottler(
            scope = childScope,
            serialApi = serialApi,
            overflowCharacteristic = overflowCharacteristic
        )
        val byteBuffer = ByteBuffer.wrap(ByteArray(4))
        byteBuffer.putInt(1024)
        overflowByteArrayFlow.emit(DataByteArray(byteBuffer.array()))
        val testBuffer = ByteArray(2500, init = { it.toByte() })
        underTest.sendBytes(testBuffer)

        childScope.advanceUntilIdle()
        childScope.advanceTimeBy(100L) // Timeout for reading buffer
        childScope.advanceUntilIdle()

        overflowByteArrayFlow.emit(DataByteArray(byteBuffer.array()))

        childScope.advanceUntilIdle()
        childScope.advanceTimeBy(100L) // Timeout for reading buffer
        childScope.advanceUntilIdle()

        overflowByteArrayFlow.emit(DataByteArray(byteBuffer.array()))

        childScope.advanceUntilIdle()
        childScope.advanceTimeBy(100L) // Timeout for reading buffer
        childScope.advanceUntilIdle()

        val firstArray = testBuffer.copyOf(1024)
        val secondArray = testBuffer.copyOfRange(1024, 2048)
        val thirdArray = testBuffer.copyOfRange(2048, testBuffer.size)
        val combineArray = firstArray + secondArray + thirdArray
        Assert.assertEquals(testBuffer.toList(), combineArray.toList())

        coVerify { serialApi.sendBytes(eq(firstArray)) }
        coVerify { serialApi.sendBytes(eq(secondArray)) }
        coVerify { serialApi.sendBytes(eq(thirdArray)) }

        childScope.cancel()
    }
}
