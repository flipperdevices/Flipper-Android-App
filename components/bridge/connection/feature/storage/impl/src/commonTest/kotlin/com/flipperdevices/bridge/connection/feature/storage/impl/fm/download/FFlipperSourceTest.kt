package com.flipperdevices.bridge.connection.feature.storage.impl.fm.download

import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.ktx.jre.split
import com.flipperdevices.protobuf.Main
import com.flipperdevices.protobuf.storage.File
import com.flipperdevices.protobuf.storage.ReadResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import okio.Buffer
import okio.ByteString.Companion.toByteString
import okio.buffer
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals

class FFlipperSourceTest {
    private lateinit var requestLooper: ReaderRequestLooper
    private lateinit var underTest: FFlipperSource

    @BeforeTest
    fun setUp() {
        requestLooper = mockk()
        underTest = FFlipperSource(requestLooper)
    }

    @Test
    fun `get one message pack`() {
        val bytes = ByteArray(512) { it.toByte() }

        val buffer = Buffer()
        coEvery { requestLooper.getNextBytePack() } returns Main(
            has_next = false,
            storage_read_response = ReadResponse(
                file_ = File(data_ = bytes.toByteString())
            )
        )

        underTest.read(buffer, byteCount = 512)

        assertContentEquals(bytes, buffer.readByteArray())
    }

    @Test
    fun `get one message fully`() {
        val bytes = ByteArray(512) { it.toByte() }

        val buffer = Buffer()
        coEvery { requestLooper.getNextBytePack() } returns Main(
            has_next = false,
            storage_read_response = ReadResponse(
                file_ = File(data_ = bytes.toByteString())
            )
        )

        val result = underTest.buffer().readByteArray()

        assertContentEquals(bytes, result)
    }

    @Test
    fun `get less bytes than message`() {
        val bytes = ByteArray(1500) { it.toByte() }

        val buffer = Buffer()
        coEvery { requestLooper.getNextBytePack() } returnsMany bytes.toMessages()

        underTest.read(buffer, 1000)

        assertContentEquals(bytes.take(1000).toByteArray(), buffer.readByteArray())
    }

    @Test
    fun `get huge message with two stage`() {
        val bytes = ByteArray(1500) { it.toByte() }

        val buffer = Buffer()
        coEvery { requestLooper.getNextBytePack() } returnsMany bytes.toMessages()

        underTest.read(buffer, 1000)
        underTest.read(buffer, 500)

        assertContentEquals(bytes, buffer.readByteArray())
    }

    @Test
    fun `get huge message once`() {
        val bytes = ByteArray(1500) { it.toByte() }

        coEvery { requestLooper.getNextBytePack() } returnsMany bytes.toMessages()

        val result = underTest.buffer().readByteArray()

        assertContentEquals(bytes, result)
    }

    @Test
    fun `get huge message with three stage`() {
        val bytes = ByteArray(2500) { it.toByte() }

        val buffer = Buffer()
        coEvery { requestLooper.getNextBytePack() } returnsMany bytes.toMessages()

        underTest.read(buffer, 600)
        underTest.read(buffer, 200)
        underTest.read(buffer, 1700)

        assertContentEquals(bytes, buffer.readByteArray())
    }

    @Test
    fun `message happens from channel`() = runTest {
        val bytes = ByteArray(512) { it.toByte() }

        val queue = Channel<Main>(Channel.UNLIMITED)
        val buffer = Buffer()
        coEvery { requestLooper.getNextBytePack() } coAnswers {
            queue.receive()
        }
        queue.send(
            Main(
                has_next = false,
                storage_read_response = ReadResponse(
                    file_ = File(data_ = bytes.toByteString())
                )
            )
        )
        val backgroundRead = backgroundScope.launch {
            underTest.read(buffer, byteCount = 512)
        }
        backgroundRead.join()

        assertContentEquals(bytes, buffer.readByteArray())
    }

    @Test
    fun `wait until message happens`() = runTest {
        val bytes = ByteArray(512) { it.toByte() }

        val queue = Channel<Main>(Channel.UNLIMITED)
        val waitFlow = Channel<Unit>()
        val buffer = Buffer()
        coEvery { requestLooper.getNextBytePack() } coAnswers {
            waitFlow.send(Unit)
            queue.receive()
        }
        val backgroundRead = backgroundScope.launch(FlipperDispatchers.workStealingDispatcher) {
            underTest.read(buffer, byteCount = 512)
        }
        waitFlow.receive()
        queue.send(
            Main(
                has_next = false,
                storage_read_response = ReadResponse(
                    file_ = File(data_ = bytes.toByteString())
                )
            )
        )
        backgroundRead.join()

        assertContentEquals(bytes, buffer.readByteArray())
    }
}

private fun ByteArray.toMessages(): List<Main> {
    val packs = split(512)
    return packs.mapIndexed { index, bytes ->
        Main(
            has_next = index != packs.lastIndex,
            storage_read_response = ReadResponse(
                file_ = File(data_ = bytes.toByteString())
            )
        )
    }
}
