package com.flipperdevices.bridge.synchronization.impl.executor

import com.flipperdevices.bridge.synchronization.impl.model.KeyAction
import com.flipperdevices.bridge.synchronization.impl.model.KeyDiff

/**
 * This class execute diff for
 */
class DiffKeyExecutor {
    /**
     * @param source
     * @param target
     */
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
