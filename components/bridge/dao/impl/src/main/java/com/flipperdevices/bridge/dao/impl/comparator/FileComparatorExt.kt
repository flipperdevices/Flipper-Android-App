package com.flipperdevices.bridge.dao.impl.comparator

import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.Files

object FileComparatorExt {
    /**
     * Checking two files for same content
     *
     * If files doesn't exists returns true
     */
    suspend fun FileComparator.isSameContent(
        file1: File,
        file2: File
    ): Boolean = withContext(FlipperDispatchers.workStealingDispatcher) {
        if (!file1.exists() && !file2.exists()) return@withContext true
        if (!file1.exists() || !file2.exists()) return@withContext false
        if (Files.size(file1.toPath()) != Files.size(file2.toPath())) {
            return@withContext false
        }
        return@withContext isSameContent(
            file1.inputStream(),
            file2.inputStream()
        )
    }
}
