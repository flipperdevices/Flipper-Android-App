package com.flipperdevices.bridge.dao.impl.api.key

import com.flipperdevices.bridge.dao.api.model.FlipperFile
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.core.ktx.jre.md5
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.wtf

object ShouldSynchronizeHelper : LogTagProvider {
    override val TAG = "ShouldSynchronizeHelper"

    suspend fun isShouldSynchronize(oldKey: FlipperKey, newKey: FlipperKey): Boolean {
        if (oldKey.mainFile.path != newKey.mainFile.path) {
            return true
        }
        if (!oldKey.mainFile.equalsAsync(newKey.mainFile)) {
            return true
        }
        if (!additionalFilesEquals(oldKey.additionalFiles, newKey.additionalFiles)) {
            return true
        }
        if (oldKey.deleted != newKey.deleted) {
            return true
        }
        return false
    }

    private suspend fun additionalFilesEquals(
        oldFiles: List<FlipperFile>,
        newFiles: List<FlipperFile>
    ): Boolean {
        if (oldFiles == newFiles) {
            return true
        }
        if (oldFiles.size != newFiles.size) {
            return false
        }
        val sortedOldFiles = oldFiles.sortedBy { it.path }
        val sortedNewFiles = newFiles.sortedBy { it.path }
        sortedOldFiles.forEachIndexed { index, oldFile ->
            val newFile = sortedNewFiles.getOrNull(index)
            if (newFile == null) {
                wtf { "Sorted new file list size should be equals" }
                return false
            }
            if (!oldFile.equalsAsync(newFile)) {
                return false
            }
        }
        return true
    }

    private suspend fun FlipperFile.equalsAsync(other: FlipperFile): Boolean {
        if (this == other) {
            return true
        }
        if (path != other.path) {
            return false
        }

        val oldMd5 = content.openStream().use { it.md5() }
        val newMd5 = other.content.openStream().use { it.md5() }
        return oldMd5 == newMd5
    }
}
