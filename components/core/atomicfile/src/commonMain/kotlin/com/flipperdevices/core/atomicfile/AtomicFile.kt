package com.flipperdevices.core.atomicfile

import okio.Path
import okio.Source

interface AtomicFile {
    suspend fun getBaseFile(): Path
    suspend fun delete()
    suspend fun startWrite(): SinkWithOutputStream
    suspend fun finishWrite(sink: SinkWithOutputStream)
    suspend fun failWrite(sink: SinkWithOutputStream)
    suspend fun openRead(): Source
    suspend fun readFully(): ByteArray
}

expect fun AtomicFile(
    baseName: Path
): AtomicFile
