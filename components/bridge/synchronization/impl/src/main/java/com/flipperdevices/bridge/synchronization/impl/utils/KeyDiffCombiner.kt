package com.flipperdevices.bridge.synchronization.impl.utils

import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.synchronization.impl.model.KeyAction
import com.flipperdevices.bridge.synchronization.impl.model.KeyDiff
import java.util.LinkedHashMap

object KeyDiffCombiner {
    fun combineKeyDiffs(
        first: List<KeyDiff>,
        second: List<KeyDiff>,
        unresolvedConflictAction: (FlipperKeyPath) -> List<KeyDiff>
    ): List<KeyDiff> {
        val resultMap = first.map {
            it.newHash.keyPath to it
        }.toMap(LinkedHashMap(first.size + second.size))

        for (keyDiff in second) {
            val keyPath = keyDiff.newHash.keyPath
            val foundedKey = resultMap[keyPath]
            if (foundedKey == null) {
                resultMap[keyPath] = keyDiff
                continue
            }
            try {
                val result = resolveConflict(foundedKey, keyDiff)
                if (result != null) {
                    resultMap[keyPath] = result
                }
            } catch (ignored: UnresolvedConflictException) {
                resultMap.remove(keyPath)
                resultMap.putAll(
                    unresolvedConflictAction(keyPath).map {
                        it.newHash.keyPath to it
                    }
                )
            }
        }

        return resultMap.values.toList()
    }

    @Throws(UnresolvedConflictException::class)
    private fun resolveConflict(first: KeyDiff, second: KeyDiff): KeyDiff? {
        return when (first.action) {
            KeyAction.ADD -> when (second.action) {
                KeyAction.ADD, KeyAction.MODIFIED ->
                    if (first.newHash.hash == second.newHash.hash) null
                    else throw UnresolvedConflictException(first, second)
                KeyAction.DELETED -> first // Impossible situation
            }
            KeyAction.MODIFIED -> when (second.action) {
                KeyAction.ADD, KeyAction.MODIFIED ->
                    if (first.newHash.hash == second.newHash.hash) null
                    else throw UnresolvedConflictException(first, second)
                KeyAction.DELETED -> KeyDiff(first.newHash, KeyAction.ADD)
            }
            KeyAction.DELETED -> when (second.action) {
                KeyAction.ADD -> second // Impossible situation
                KeyAction.MODIFIED -> KeyDiff(second.newHash, KeyAction.ADD)
                KeyAction.DELETED -> null
            }
        }
    }
}

private class UnresolvedConflictException(first: KeyDiff, second: KeyDiff) : RuntimeException()
