package com.flipperdevices.bridge.synchronization.impl.executor

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

    suspend fun execute(source: AbstractKeyStorage, target: AbstractKeyStorage, diff: KeyDiff) {
        val path = diff.hashedKey.keyPath

        when (diff.action) {
            KeyAction.ADD -> {
                val content = source.loadKey(path)
                target.saveKey(path, content)
            }
            KeyAction.MODIFIED -> {
                target.deleteKey(path)
                val content = source.loadKey(path)
                target.saveKey(path, content)
            }
            KeyAction.DELETED -> target.deleteKey(path)
        }
    }
}
