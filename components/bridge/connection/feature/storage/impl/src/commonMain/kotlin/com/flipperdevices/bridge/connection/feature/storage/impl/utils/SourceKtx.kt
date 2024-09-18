package com.flipperdevices.bridge.connection.feature.storage.impl.utils

import com.flipperdevices.bridge.connection.pbutils.ProtobufConstants
import com.flipperdevices.core.progress.FixedProgressListener
import okio.Buffer
import okio.Sink
import okio.Source

suspend fun Source.copyWithProgress(
    sink: Sink,
    progressListener: FixedProgressListener? = null,
    sourceLength: (suspend () -> Long?)? = null
) {
    val calculatedLength = if (progressListener != null) {
        sourceLength?.invoke()
    } else {
        null
    }

    var totalBytesRead = 0L
    val buffer = Buffer()
    while (true) {
        val readCount: Long = read(buffer, ProtobufConstants.MAX_FILE_DATA.toLong())
        if (readCount == -1L) break
        sink.write(buffer, readCount)
        totalBytesRead += readCount
        if (calculatedLength != null && progressListener != null) {
            progressListener.onProgress(totalBytesRead, calculatedLength)
        }
    }
}
