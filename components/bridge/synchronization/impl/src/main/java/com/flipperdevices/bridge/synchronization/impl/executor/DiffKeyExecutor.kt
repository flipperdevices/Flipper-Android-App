package com.flipperdevices.bridge.synchronization.impl.executor

import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.synchronization.impl.model.KeyAction
import com.flipperdevices.bridge.synchronization.impl.model.KeyDiff
import com.flipperdevices.core.log.LogTagProvider

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
        diffs: List<KeyDiff>
    ): List<KeyDiff> {
        diffs.forEach {
            try {
                execute(source, target, it)
            } catch (e: Exception) {
                throw e
            }
        }
        return diffs
    }

    private suspend fun execute(
        source: AbstractKeyStorage,
        target: AbstractKeyStorage,
        diff: KeyDiff
    ) {
        val path = diff.hashedKey.keyPath
        val targetPath = FlipperKeyPath(path.fileType!!.flipperDir, path.name)

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
