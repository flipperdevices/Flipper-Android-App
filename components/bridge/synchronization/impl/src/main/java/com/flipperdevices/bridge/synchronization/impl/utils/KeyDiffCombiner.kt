package com.flipperdevices.bridge.synchronization.impl.utils

import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperFileType
import com.flipperdevices.bridge.synchronization.impl.model.DiffSource
import com.flipperdevices.bridge.synchronization.impl.model.KeyAction
import com.flipperdevices.bridge.synchronization.impl.model.KeyDiff

object KeyDiffCombiner {
    @Throws(UnresolvedConflictException::class)
    fun combineKeyDiffs(
        first: List<KeyDiff>,
        second: List<KeyDiff>
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
            val result = resolveConflict(foundedKey, keyDiff)
            if (result != null) {
                resultMap[keyPath] = result
            }
        }

        return resultMap.values.toList()
    }

    @Throws(UnresolvedConflictException::class)
    @Suppress("ComplexMethod")
    private fun resolveConflict(first: KeyDiff, second: KeyDiff): KeyDiff? {
        /**
         * If the file is shadow, we always give priority
         * to a copy of the file from the flipper.
         */
        if (first.newHash.keyPath.fileType == FlipperFileType.SHADOW_NFC) {
            if (first.source == DiffSource.FLIPPER) {
                return first
            }
            if (second.source == DiffSource.FLIPPER) {
                return second
            }
        }

        return when (first.action) {
            KeyAction.ADD -> when (second.action) {
                KeyAction.ADD, KeyAction.MODIFIED ->
                    if (first.newHash.hash == second.newHash.hash) null
                    else throw UnresolvedConflictException(first.newHash.keyPath)
                KeyAction.DELETED -> first // Impossible situation
            }
            KeyAction.MODIFIED -> when (second.action) {
                KeyAction.ADD, KeyAction.MODIFIED ->
                    if (first.newHash.hash == second.newHash.hash) null
                    else throw UnresolvedConflictException(first.newHash.keyPath)
                KeyAction.DELETED -> KeyDiff(first.newHash, KeyAction.ADD, first.source)
            }
            KeyAction.DELETED -> when (second.action) {
                KeyAction.ADD -> second // Impossible situation
                KeyAction.MODIFIED -> KeyDiff(second.newHash, KeyAction.ADD, second.source)
                KeyAction.DELETED -> null
            }
        }
    }
}

class UnresolvedConflictException(val path: FlipperFilePath) : RuntimeException()
