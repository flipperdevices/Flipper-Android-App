package com.flipperdevices.bridge.synchronization.impl.repository.manifest

import com.flipperdevices.bridge.synchronization.impl.model.KeyAction
import com.flipperdevices.bridge.synchronization.impl.model.KeyDiff
import com.flipperdevices.bridge.synchronization.impl.model.KeyWithHash

object ManifestChangeExecutor {
    fun applyChanges(source: List<KeyWithHash>, diffs: List<KeyDiff>): List<KeyWithHash> {
        val sourceAsMap = source.map { it.keyPath to it.hash }.toMap(LinkedHashMap())
        diffs.forEach { diff ->
            when (diff.action) {
                KeyAction.ADD,
                KeyAction.MODIFIED -> sourceAsMap[diff.newHash.keyPath] = diff.newHash.hash
                KeyAction.DELETED -> sourceAsMap.remove(diff.newHash.keyPath)
            }
        }
        return sourceAsMap.map { KeyWithHash(it.key, it.value) }
    }
}
