package com.flipperdevices.core.ktx.jre

import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream

/**
 * Copies this stream to the given output stream, returning the number of bytes copied
 *
 * **Note** It is the caller's responsibility to close both of these resources.
 */
suspend fun InputStream.copyTo(
    out: OutputStream,
    bufferSize: Int = DEFAULT_BUFFER_SIZE,
    onProcessed: suspend (Long) -> Unit
): Long = withContext(FlipperDispatchers.workStealingDispatcher) {
    var bytesCopied: Long = 0
    val buffer = ByteArray(bufferSize)
    var bytes = read(buffer)
    while (bytes >= 0) {
        out.write(buffer, 0, bytes)
        bytesCopied += bytes
        bytes = read(buffer)
        onProcessed(bytesCopied)
    }
    return@withContext bytesCopied
}
