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
        val firstKeysAsMap = first.associateBy {
            it.newHash.keyPath
        }.toMutableMap()
        val resultMap = LinkedHashMap<FlipperFilePath, KeyDiff>()

        for (keyDiff in second) {
            val keyPath = keyDiff.newHash.keyPath
            val foundedKey = firstKeysAsMap.remove(keyPath)
            if (foundedKey == null) {
                resultMap[keyPath] = keyDiff
                continue
            }
            val result = resolveConflict(foundedKey, keyDiff)
            if (result != null) {
                resultMap[keyPath] = result
            }
        }

        return firstKeysAsMap.values + resultMap.values
    }

    @Throws(UnresolvedConflictException::class)
    @Suppress("ComplexMethod")
    private fun resolveConflict(first: KeyDiff, second: KeyDiff): KeyDiff? {
        return when (first.action) {
            KeyAction.ADD -> when (second.action) {
                KeyAction.ADD, KeyAction.MODIFIED -> resolveConflictBothAdd(first, second)
                KeyAction.DELETED -> first // Impossible situation
            }
            KeyAction.MODIFIED -> when (second.action) {
                KeyAction.ADD, KeyAction.MODIFIED -> resolveConflictBothAdd(first, second)
                KeyAction.DELETED -> KeyDiff(first.newHash, KeyAction.ADD, first.source)
            }
            KeyAction.DELETED -> when (second.action) {
                KeyAction.ADD -> second // Impossible situation
                KeyAction.MODIFIED -> KeyDiff(second.newHash, KeyAction.ADD, second.source)
                KeyAction.DELETED -> null
            }
        }
    }

    private fun resolveConflictBothAdd(first: KeyDiff, second: KeyDiff): KeyDiff? {
        return if (first.newHash.hash == second.newHash.hash) {
            null
        } else if (listOf(FlipperFileType.SHADOW_NFC, FlipperFileType.UI_INFRARED)
                .contains(first.newHash.keyPath.fileType)
        ) {
            /**
             * If the file is shadow, we always give priority
             * to a copy of the file from the flipper.
             */
            if (first.source == DiffSource.FLIPPER) {
                first
            } else if (second.source == DiffSource.FLIPPER) {
                second
            } else {
                throw UnresolvedConflictException(first.newHash.keyPath) // Impossible situation
            }
        } else {
            throw UnresolvedConflictException(first.newHash.keyPath)
        }
    }
}

class UnresolvedConflictException(val path: FlipperFilePath) : RuntimeException()
