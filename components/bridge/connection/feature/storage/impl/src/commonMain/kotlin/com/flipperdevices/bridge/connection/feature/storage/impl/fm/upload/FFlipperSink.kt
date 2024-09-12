package com.flipperdevices.bridge.connection.feature.storage.impl.fm.upload

import com.flipperdevices.bridge.connection.pbutils.ProtobufConstants
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.warn
import okio.Buffer
import okio.Sink
import okio.Timeout
import kotlin.math.min

internal class FFlipperSink(
    private val requestLooper: WriteRequestLooper,
    private val chunkSize: Long = ProtobufConstants.MAX_FILE_DATA.toLong()
) : Sink, LogTagProvider {
    override val TAG = "FFlipperSink"

    private val buffer = Buffer()

    @Synchronized
    override fun close() {
        while (buffer.size > chunkSize) {
            requestLooper.writeSync(buffer.readByteString(min(buffer.size, chunkSize)))
        }

        requestLooper.writeSync(buffer.readByteString(), hasNext = false)

        requestLooper.awaitResult().getOrThrow()
    }

    @Synchronized
    override fun flush() {
        while (buffer.size != 0L) {
            requestLooper.writeSync(buffer.readByteString(min(buffer.size, chunkSize)))
        }
    }

    override fun timeout() = Timeout.NONE

    @Synchronized
    override fun write(source: Buffer, byteCount: Long) {
        var bytesRemaining = byteCount
        var leftToWrite = getLeftToWrite()
        while (bytesRemaining > leftToWrite) {
            buffer.write(source, leftToWrite)
            bytesRemaining -= leftToWrite
            flush()
            leftToWrite = getLeftToWrite()
        }
        buffer.write(source, bytesRemaining)
    }

    private fun getLeftToWrite(): Long {
        while (buffer.size > chunkSize) {
            warn { "It's not normal that we have a buffer overflow at this point." }
            requestLooper.writeSync(buffer.readByteString(min(buffer.size, chunkSize)))
        }

        return chunkSize - buffer.size
    }
}
