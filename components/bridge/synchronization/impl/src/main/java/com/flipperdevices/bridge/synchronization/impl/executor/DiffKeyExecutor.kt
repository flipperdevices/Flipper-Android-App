package com.flipperdevices.bridge.synchronization.impl.executor

import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
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
        return diffs.mapIndexedNotNull { index, diff ->
            try {
                info { "Execute $diff for $source to $target" }
                execute(source, target, diff)
                onProgressUpdate?.invoke(index, diffs.size)
                return@mapIndexedNotNull diff
            } catch (
                @Suppress("detekt:TooGenericExceptionCaught")
                executeError: Exception
            ) {
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
        val targetPath = FlipperKeyPath(
            path.fileType!!.flipperDir,
            path.name,
            deleted = false
        )

        when (diff.action) {
            KeyAction.ADD -> {
                val content = source.loadKey(path)
                target.saveKey(targetPath, content)
            }
            KeyAction.MODIFIED -> {
                val content = source.loadKey(path)
                target.deleteKey(targetPath)
                target.saveKey(targetPath, content)
            }
            KeyAction.DELETED -> target.deleteKey(targetPath)
        }
    }
}
