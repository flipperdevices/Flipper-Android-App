package com.flipperdevices.core.ktx.jre

import okio.Buffer
import okio.ForwardingSource
import okio.IOException
import okio.Source

// Copy from okio.internal.FixedLengthSource
internal class FixedLengthSource(
    delegate: Source,
    private val size: Long,
    private val truncate: Boolean,
    private val throwOnByteShortage: Boolean
) : ForwardingSource(delegate) {
    private var bytesReceived = 0L

    override fun read(sink: Buffer, byteCount: Long): Long {
        // Figure out how many bytes to attempt to read.
        //
        // If we're truncating, we never attempt to read more than what's remaining.
        //
        // Otherwise we expect the underlying source to be exactly the promised size. Read as much as
        // possible and throw an exception if too many bytes are returned.
        val toRead = when {
            bytesReceived > size -> 0L // Already read more than the promised size.
            truncate -> {
                val remaining = size - bytesReceived
                if (remaining == 0L) return -1L // Already read exactly the promised size.
                minOf(byteCount, remaining)
            }

            else -> byteCount
        }

        val result = super.read(sink, toRead)

        if (result != -1L) bytesReceived += result

        // Throw an exception if we received too few bytes or too many.
        if (throwOnByteShortage && bytesReceived < size && result == -1L) {
            throw IOException("expected $size bytes but got $bytesReceived")
        }

        // Throw an exception if we received too many bytes.
        if (bytesReceived > size) {
            if (result > 0L && bytesReceived > size) {
                // If we received bytes beyond the limit, don't return them to the caller.
                sink.truncateToSize(sink.size - (bytesReceived - size))
            }
            throw IOException("expected $size bytes but got $bytesReceived")
        }

        return result
    }

    private fun Buffer.truncateToSize(newSize: Long) {
        val scratch = Buffer()
        scratch.writeAll(this)
        write(scratch, newSize)
        scratch.clear()
    }
}

fun Source.limit(size: Long, throwOnByteShortage: Boolean = false): Source {
    return FixedLengthSource(
        delegate = this,
        size = size,
        truncate = true,
        throwOnByteShortage = throwOnByteShortage
    )
}
