package com.flipperdevices.bridge.dao.impl.comparator

import com.flipperdevices.core.FlipperStorageProvider
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import kotlinx.coroutines.withContext
import okio.BufferedSource
import okio.FileSystem
import okio.Path
import okio.buffer
import javax.inject.Inject

class FileComparator @Inject constructor(
    private val flipperStorageProvider: FlipperStorageProvider
) {

    /**
     * Check if two streams have identical content
     */
    suspend fun isSameContent(
        source1: BufferedSource,
        source2: BufferedSource
    ): Boolean = withContext(FlipperDispatchers.workStealingDispatcher) {
        val isSource1Empty = source1.request(byteCount = 1).not()
        val isSource2Empty = source2.request(byteCount = 1).not()
        if (isSource1Empty && isSource2Empty) {
            return@withContext true
        }
        if (isSource1Empty || isSource2Empty) {
            return@withContext false
        }
        do {
            val ch1 = runCatching { source1.readByte() }.getOrNull()
            val ch2 = runCatching { source2.readByte() }.getOrNull()
            if (ch1 != ch2) return@withContext false
        } while (ch1 != null)
        return@withContext true
    }

    /**
     * Checking two files for same content
     *
     * If files doesn't exists returns true
     */
    suspend fun isSameContent(
        file1: Path,
        file2: Path,
        fileSystem: FileSystem = flipperStorageProvider.fileSystem
    ): Boolean = withContext(FlipperDispatchers.workStealingDispatcher) {
        if (!fileSystem.exists(file1) && !fileSystem.exists(file2)) return@withContext true
        if (!fileSystem.exists(file1) || !fileSystem.exists(file2)) return@withContext false

        if (fileSystem.metadataOrNull(file1)?.size != fileSystem.metadataOrNull(file2)?.size) {
            return@withContext false
        }
        return@withContext fileSystem.source(file1).buffer().use { source1 ->
            fileSystem.source(file2).buffer().use { source2 ->
                isSameContent(
                    source1,
                    source2
                )
            }
        }
    }
}
