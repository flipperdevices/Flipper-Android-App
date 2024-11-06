package com.flipperdevices.bridge.connection.ble.impl.serial

import android.content.Context
import android.content.pm.PackageManager
import com.flipperdevices.bridge.connection.feature.actionnotifier.api.FlipperActionNotifier
import com.flipperdevices.bridge.connection.transport.ble.impl.serial.FSerialOverflowThrottler
import com.flipperdevices.bridge.connection.transport.common.api.serial.FSerialDeviceApi
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
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
import no.nordicsemi.android.kotlin.ble.client.main.service.ClientBleGattCharacteristic
import no.nordicsemi.android.kotlin.ble.core.data.util.DataByteArray
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.nio.ByteBuffer

class FSerialOverflowThrottlerTest {
    private lateinit var overflowByteArrayFlow: MutableSharedFlow<DataByteArray>
    private lateinit var overflowCharacteristic: ClientBleGattCharacteristic
    private lateinit var serialApi: FSerialDeviceApi
    private lateinit var context: Context
    private lateinit var flipperActionNotifier: FlipperActionNotifier

    @Before
    fun setUp() {
        serialApi = mockk(relaxUnitFun = true)
        overflowByteArrayFlow = MutableSharedFlow(replay = 1)
        flipperActionNotifier = mockk(relaxed = true)
        overflowCharacteristic = mockk(relaxUnitFun = true) {
            coEvery { getNotifications(any(), any()) } returns overflowByteArrayFlow
            coEvery { read() } returns DataByteArray()
        }
        context = mockk {
            every { checkSelfPermission(any()) } returns PackageManager.PERMISSION_GRANTED
        }
    }

    @Test
    fun `send request if we fit in buffer`() = runTest {
        val childScope = TestScope(this.testScheduler)
        val underTest = FSerialOverflowThrottler(
            scope = childScope,
            serialApi = serialApi,
            overflowCharacteristic = overflowCharacteristic,
            context = context,
            flipperActionNotifier = flipperActionNotifier
        )
        val byteBuffer = ByteBuffer.wrap(ByteArray(4))
        byteBuffer.putInt(1024)
        overflowByteArrayFlow.emit(DataByteArray(byteBuffer.array()))

        childScope.advanceUntilIdle()

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
            overflowCharacteristic = overflowCharacteristic,
            context = context,
            flipperActionNotifier = flipperActionNotifier
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
            overflowCharacteristic = overflowCharacteristic,
            context = context,
            flipperActionNotifier = flipperActionNotifier
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
            overflowCharacteristic = overflowCharacteristic,
            context = context,
            flipperActionNotifier = flipperActionNotifier
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
            overflowCharacteristic = overflowCharacteristic,
            context = context,
            flipperActionNotifier = flipperActionNotifier
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
            overflowCharacteristic = overflowCharacteristic,
            context = context,
            flipperActionNotifier = flipperActionNotifier
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
            overflowCharacteristic = overflowCharacteristic,
            context = context,
            flipperActionNotifier = flipperActionNotifier
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

    @Test
    fun `when send multiple async requests then no bytes missing`() = runTest {
        val childScope = TestScope(this.testScheduler)
        val serialDeviceApi = FSerialOverflowThrottler(
            scope = childScope,
            serialApi = serialApi,
            overflowCharacteristic = overflowCharacteristic,
            context = context,
            flipperActionNotifier = flipperActionNotifier
        )

        fun getByteArray() = ByteArray(4)
            .let(ByteBuffer::wrap)
            .apply { putInt(1024) }
            .array()
            .let(::DataByteArray)

        fun indexFilledByteArray(size: Int) = ByteArray(
            size = size,
            init = { index -> index.toByte() }
        )

        overflowByteArrayFlow.emit(getByteArray())
        // 2500 in total
        // Need to be delivered separately and async
        listOf(
            512,
            512,
            512,
            512,
            452
        ).forEach { size -> launch { serialDeviceApi.sendBytes(indexFilledByteArray(size)) } }
        // First portion of 1024
        childScope.advanceUntilIdle()
        childScope.advanceTimeBy(100L) // Timeout for reading buffer
        childScope.advanceUntilIdle()
        overflowByteArrayFlow.emit(getByteArray())

        // Second portion of 1024
        childScope.advanceUntilIdle()
        childScope.advanceTimeBy(100L) // Timeout for reading buffer
        childScope.advanceUntilIdle()
        overflowByteArrayFlow.emit(getByteArray())

        // Last portion of 452
        childScope.advanceUntilIdle()
        childScope.advanceTimeBy(100L) // Timeout for reading buffer
        childScope.advanceUntilIdle()
        overflowByteArrayFlow.emit(getByteArray())

        coVerify { serialApi.sendBytes(eq(indexFilledByteArray(1024))) }
        coVerify { serialApi.sendBytes(eq(indexFilledByteArray(1024))) }
        coVerify { serialApi.sendBytes(eq(indexFilledByteArray(452))) }

        childScope.cancel()
    }
}
