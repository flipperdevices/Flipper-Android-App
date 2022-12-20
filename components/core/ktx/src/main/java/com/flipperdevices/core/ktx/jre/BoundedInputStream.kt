package com.flipperdevices.core.ktx.jre

import java.io.IOException
import java.io.InputStream

private const val EOF = -1

/**
 * An `InputStream` wrapper that provides up to a maximum number of
 * bytes from the underlying stream.  Does not support mark/reset, even
 * when the wrapped stream does, and does not perform any buffering.
 */
class BoundedInputStream(
    /** This stream's underlying @{code InputStream}  */
    private val data: InputStream,
    maxBytes: Long
) : InputStream() {
    /** The maximum number of bytes still available from this stream  */
    private var bytesRemaining: Long

    /**
     * Initializes a new `BoundedInputStream` with the specified
     * underlying stream and byte limit
     * @param data the @{code InputStream} serving as the source of this
     * one's data
     * @param maxBytes the maximum number of bytes this stream will deliver
     * before signaling end-of-data
     */
    init {
        bytesRemaining = maxBytes.coerceAtLeast(0)
    }

    @Throws(IOException::class)
    override fun available(): Int {
        return Math.min(data.available().toLong(), bytesRemaining).toInt()
    }

    @Throws(IOException::class)
    override fun close() {
        data.close()
    }

    @Synchronized
    override fun mark(limit: Int) {
        // does nothing
    }

    override fun markSupported(): Boolean {
        return false
    }

    @Throws(IOException::class)
    override fun read(buf: ByteArray, off: Int, len: Int): Int {
        return if (bytesRemaining > 0) {
            val nRead = data.read(
                buf,
                off,
                Math.min(len.toLong(), bytesRemaining).toInt()
            )
            bytesRemaining -= nRead.toLong()
            nRead
        } else {
            EOF
        }
    }

    @Throws(IOException::class)
    override fun read(buf: ByteArray): Int {
        return this.read(buf, 0, buf.size)
    }

    @Synchronized
    @Throws(IOException::class)
    override fun reset() {
        throw IOException("reset() not supported")
    }

    @Throws(IOException::class)
    override fun skip(n: Long): Long {
        val skipped = data.skip(Math.min(n, bytesRemaining))
        bytesRemaining -= skipped
        return skipped
    }

    @Throws(IOException::class)
    override fun read(): Int {
        return if (bytesRemaining > 0) {
            val readedBytes = data.read()
            if (readedBytes >= 0) {
                bytesRemaining -= 1
            }
            readedBytes
        } else {
            EOF
        }
    }
}
