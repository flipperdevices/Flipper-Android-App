package com.flipperdevices.bridge.synchronization.impl.executor

import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperFileType
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.bridge.synchronization.impl.model.KeyAction
import com.flipperdevices.bridge.synchronization.impl.model.KeyDiff
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info

/**
 * This class execute diff for
 */
class DiffKeyExecutor : LogTagProvider {
    override val TAG = "DiffKeyExecutor"

    /**
     * @return list of diffs which successful applied
     */
    suspend fun executeBatch(
        source: AbstractKeyStorage,
        target: AbstractKeyStorage,
        diffs: List<KeyDiff>,
        onProgressUpdate: (suspend (Int, Int) -> Unit)? = null
    ): List<KeyDiff> {
        val sortedDiffs = diffs.sortedBy { it.newHash.keyPath.fileType.ordinal }
        return sortedDiffs
            .mapIndexedNotNull { index, diff ->
                try {
                    info { "Execute $diff for $source to $target" }
                    execute(source, target, diff)
                    onProgressUpdate?.invoke(index, diffs.size)
                    return@mapIndexedNotNull diff
                } catch (executeError: Exception) {
                    error(executeError) { "While apply diff $diff we have error" }
                }
                return@mapIndexedNotNull null
            }
    }

    private suspend fun execute(
        source: AbstractKeyStorage,
        target: AbstractKeyStorage,
        diff: KeyDiff
    ) {
        val path = diff.newHash.keyPath
        val folder = when (path.fileType) {
            FlipperFileType.KEY -> path.keyType!!.flipperDir
            FlipperFileType.SHADOW_NFC -> FlipperKeyType.NFC.flipperDir
            FlipperFileType.OTHER -> error("Don't support file with this type")
        }
        val targetPath = FlipperFilePath(
            folder,
            path.nameWithExtension
        )

        when (diff.action) {
            KeyAction.ADD -> {
                val content = source.loadFile(path)
                target.saveFile(targetPath, content)
            }
            KeyAction.MODIFIED -> {
                val content = source.loadFile(path)
                target.modify(targetPath, content)
            }
            KeyAction.DELETED -> target.deleteFile(targetPath)
        }
    }
}
