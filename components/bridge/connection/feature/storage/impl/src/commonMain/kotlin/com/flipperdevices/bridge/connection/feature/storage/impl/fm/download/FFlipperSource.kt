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
        var readBytesCount = buffer.read(sink, byteCount)
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
                    remainingBytesToRead.toInt(), packByteArray.size
                )
                sink.read(toRead)
                buffer.write(toBuffer)
            } else {
                sink.read(newPack.toByteArray())
                readBytesCount += newPack.size
            }
            if (newResponse.has_next.not()) {
                if (readBytesCount > byteCount) {
                    isFinished = true
                    sink.read(byteArrayOf(EOF))
                    return readBytesCount
                } else {
                    buffer.write(byteArrayOf(EOF))
                }
            }
        }
        return readBytesCount
    }

    override fun timeout() = Timeout.NONE
}