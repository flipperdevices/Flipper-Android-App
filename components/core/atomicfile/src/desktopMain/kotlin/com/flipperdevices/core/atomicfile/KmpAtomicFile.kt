package com.flipperdevices.core.atomicfile

import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.FileSystem
import okio.IOException
import okio.Path
import okio.Sink
import okio.Source
import okio.buffer

class KmpAtomicFile(
    private val mBaseName: Path,
    private val fileSystem: FileSystem = FileSystem.SYSTEM
) : AtomicFile, LogTagProvider {
    override val TAG = "KmpAtomicFile"

    private var mNewName = mBaseName.resolve(".new")
    private var mLegacyBackupName = mBaseName.resolve(".bak")

    override suspend fun getBaseFile() = mBaseName

    override suspend fun delete() {
        fileSystem.delete(mBaseName)
        fileSystem.delete(mNewName)
        fileSystem.delete(mLegacyBackupName)
    }

    override suspend fun startWrite(): SinkWithOutputStream = withContext(Dispatchers.IO) {
        if (fileSystem.exists(mLegacyBackupName)) {
            rename(mLegacyBackupName, mBaseName)
        }

        return@withContext try {
            fileSystem.sink(mNewName)
        } catch (e: IOException) {
            error(e) { "Failed sink, try create directory" }
            val parent = mNewName.parent ?: throw IOException("Failed to find parent for $mNewName")
            val isMkdirs = runCatching {
                fileSystem.createDirectory(parent, mustCreate = true)
            }.isSuccess
            if (!isMkdirs) {
                throw IOException("Failed to create directory for $mNewName")
            }

            fileSystem.sink(mNewName)
        }.wrap()
    }

    override suspend fun finishWrite(sink: SinkWithOutputStream) {
        if (!sync(sink)) {
            error { "Failed to sync file output stream" }
        }
        try {
            sink.flush()
            sink.close()
        } catch (e: IOException) {
            error(e) { "Failed to close file output stream" }
        }
        rename(mNewName, mBaseName)
    }

    override suspend fun openRead(): Source = withContext(Dispatchers.IO) {
        if (fileSystem.exists(mLegacyBackupName)) {
            rename(mLegacyBackupName, mBaseName)
        }

        // It was okay to call openRead() between startWrite() and finishWrite() for the first time
        // (because there is no backup file), where openRead() would open the file being written,
        // which makes no sense, but finishWrite() would still persist the write properly. For all
        // subsequent writes, if openRead() was called in between, it would see a backup file and
        // delete the file being written, the same behavior as our new implementation. So we only
        // need a special case for the first write, and don't delete the new file in this case so
        // that finishWrite() can still work.
        if (fileSystem.exists(mNewName) && fileSystem.exists(mBaseName)) {
            val isDelete = runCatching {
                fileSystem.delete(mNewName)
            }.isSuccess
            if (!isDelete) {
                error { "Failed to delete outdated new file $mNewName" }
            }
        }
        return@withContext fileSystem.source(mBaseName)
    }

    /**
     * A convenience for [.openRead] that also reads all of the
     * file contents into a byte array which is returned.
     */
    override suspend fun readFully() = withContext(Dispatchers.IO) {
        return@withContext openRead().use {
            it.buffer().readByteArray()
        }
    }

    override suspend fun failWrite(sink: SinkWithOutputStream) = withContext(Dispatchers.IO) {
        if (!sync(sink)) {
            error { "Failed to sync file output stream" }
        }
        try {
            sink.close()
        } catch (e: java.io.IOException) {
            error(e) { "Failed to close file output stream" }
        }
        val isDelete = runCatching { fileSystem.delete(mNewName) }.isSuccess

        if (!isDelete) {
            error { "Failed to delete new file $mNewName" }
        }
    }

    @Suppress("UnusedParameter", "FunctionOnlyReturningConstant")
    private fun sync(stream: Sink) = true

    private fun rename(source: Path, target: Path) {
        // We used to delete the target file before rename, but that isn't atomic, and the rename()
        // syscall should atomically replace the target file. However in the case where the target
        // file is a directory, a simple rename() won't work. We need to delete the file in this
        // case because there are callers who erroneously called mBaseName.mkdirs() (instead of
        // mBaseName.getParentFile().mkdirs()) before creating the AtomicFile, and it worked
        // regardless, so this deletion became some kind of API.

        if (fileSystem.metadata(target).isDirectory) {
            val isDelete = runCatching { fileSystem.delete(target) }.isSuccess
            if (!isDelete) {
                error { "Failed to delete file which is a directory $target" }
            }
        }
        val isMoving = runCatching { fileSystem.atomicMove(source, target) }.isSuccess
        if (!isMoving) {
            error { "Failed to rename $source to $target" }
        }
    }
}
