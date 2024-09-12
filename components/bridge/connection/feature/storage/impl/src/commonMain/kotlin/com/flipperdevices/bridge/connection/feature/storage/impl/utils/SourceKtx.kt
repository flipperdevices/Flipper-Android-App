package com.flipperdevices.bridge.connection.feature.storage.impl.utils

import com.flipperdevices.bridge.connection.pbutils.ProtobufConstants
import com.flipperdevices.core.progress.ProgressListener
import com.flipperdevices.core.progress.ProgressWrapperTracker
import okio.Buffer
import okio.Sink
import okio.Source

suspend fun Source.copyWithProgress(
    sink: Sink,
    progressListener: ProgressListener? = null,
    sourceLength: (suspend () -> Long?)? = null
) {
    val progressWrapper = progressListener?.let { ProgressWrapperTracker(it) }
    val calculatedLength = if (progressListener != null) {
        sourceLength?.invoke()
    } else null

    var totalBytesRead = 0L
    val buffer = Buffer()
    while (true) {
        val readCount: Long = read(buffer, ProtobufConstants.MAX_FILE_DATA.toLong())
        if (readCount == -1L) break
        sink.write(buffer, readCount)
        totalBytesRead += readCount
        if (calculatedLength != null && progressWrapper != null) {
            progressWrapper.report(totalBytesRead, calculatedLength)
        }
    }
}