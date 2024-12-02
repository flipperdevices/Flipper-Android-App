package com.flipperdevices.core.progress

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import okio.Buffer
import okio.Sink
import okio.Source

private const val DEFAULT_CHUNK_SIZE = 512L

suspend fun Source.copyWithProgress(
    sink: Sink,
    progressListener: FixedProgressListener? = null,
    sourceLength: (suspend () -> Long?)? = null,
    chunkSize: Long = DEFAULT_CHUNK_SIZE
) {
    val calculatedLength = if (progressListener != null) {
        sourceLength?.invoke()
    } else {
        null
    }

    var totalBytesRead = 0L
    val buffer = Buffer()
    while (currentCoroutineContext().isActive) {
        val readCount: Long = read(buffer, chunkSize)
        if (readCount == -1L) break
        sink.write(buffer, readCount)
        totalBytesRead += readCount
        if (calculatedLength != null && progressListener != null) {
            progressListener.onProgress(totalBytesRead, calculatedLength)
        }
    }
}
