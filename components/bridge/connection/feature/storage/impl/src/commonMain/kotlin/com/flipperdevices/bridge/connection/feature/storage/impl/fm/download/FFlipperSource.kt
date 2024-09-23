package com.flipperdevices.bridge.connection.feature.storage.impl.fm.download

import kotlinx.coroutines.runBlocking
import okio.Buffer
import okio.Source
import okio.Timeout

private const val EOF: Byte = -1

class FFlipperSource(
    private val readerLoop: ReaderRequestLooper
) : Source {
    private var isFinished = false
    private val buffer = Buffer()

    override fun close() = readerLoop.close()

    @Synchronized
    override fun read(sink: Buffer, byteCount: Long): Long {
        if (isFinished) {
            return EOF.toLong()
        }
        if (byteCount <= buffer.size) {
            return buffer.read(sink, byteCount)
        }
        var readBytesCount = 0L
        if (buffer.size > 0L) {
            readBytesCount = buffer.readAll(sink)
        }
        while (readBytesCount < byteCount) {
            val newResponse = runBlocking { readerLoop.getNextBytePack() }
            val newPack = newResponse.storage_read_response?.file_?.data_
            requireNotNull(newPack) {
                "Storage read response not found. " +
                    "Response is: $newPack"
            }
            val remainingBytesToRead = byteCount - readBytesCount

            if (newPack.size > remainingBytesToRead) {
                val packByteArray = newPack.toByteArray()
                val toRead = packByteArray.copyOf(remainingBytesToRead.toInt())
                val toBuffer = packByteArray.copyOfRange(
                    remainingBytesToRead.toInt(),
                    packByteArray.size
                )
                sink.write(toRead)
                readBytesCount += toRead.size
                buffer.write(toBuffer)
            } else {
                sink.write(newPack.toByteArray())
                readBytesCount += newPack.size
            }
            if (newResponse.has_next.not()) {
                isFinished = true
                return readBytesCount
            }
        }
        return readBytesCount
    }

    override fun timeout() = Timeout.NONE
}
