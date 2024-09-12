package com.flipperdevices.core.atomicfile

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.Path.Companion.toOkioPath
import okio.source

class AndroidAtomicFile(
    private val androidAtomicFile: androidx.core.util.AtomicFile
) : AtomicFile {
    override suspend fun getBaseFile() = androidAtomicFile.baseFile.toOkioPath()

    override suspend fun delete() = withContext(Dispatchers.IO) {
        androidAtomicFile.delete()
    }

    override suspend fun startWrite() = SinkWithOutputStream(androidAtomicFile.startWrite())

    override suspend fun finishWrite(sink: SinkWithOutputStream) = withContext(Dispatchers.IO) {
        sink.flush()
        androidAtomicFile.finishWrite(sink.outputStream)
    }

    override suspend fun failWrite(sink: SinkWithOutputStream) {
        androidAtomicFile.failWrite(sink.outputStream)
    }

    override suspend fun openRead() = androidAtomicFile.openRead().source()

    override suspend fun readFully() = withContext(Dispatchers.IO) { androidAtomicFile.readFully() }
}
