package com.flipperdevices.bridge.synchronization.impl.repository.manifest

import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.UpdateKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.UtilsKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.synchronization.impl.di.TaskGraph
import com.flipperdevices.bridge.synchronization.impl.model.KeyDiff
import com.flipperdevices.bridge.synchronization.impl.model.RestartSynchronizationException
import com.flipperdevices.bridge.synchronization.impl.utils.KeyDiffCombiner
import com.flipperdevices.bridge.synchronization.impl.utils.UnresolvedConflictException
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.warn
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

interface DiffMergeHelper {
    suspend fun mergeDiffs(
        first: List<KeyDiff>, second: List<KeyDiff>
    ): List<KeyDiff>
}

@ContributesBinding(TaskGraph::class, DiffMergeHelper::class)
class DiffMergeHelperImpl @Inject constructor(
    private val simpleKeyApi: SimpleKeyApi,
    private val utilsKeyApi: UtilsKeyApi,
    private val updateKeyApi: UpdateKeyApi
) : DiffMergeHelper, LogTagProvider {
    override val TAG = "DiffMergeHelper"

    override suspend fun mergeDiffs(
        first: List<KeyDiff>, second: List<KeyDiff>
    ): List<KeyDiff> {
        return try {
            KeyDiffCombiner.combineKeyDiffs(
                first,
                second
            )
        } catch (conflict: UnresolvedConflictException) {
            try {
                resolveConflict(FlipperKeyPath(conflict.path, deleted = false))
            } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
                error(e) { "Error during resolve conflict $conflict" }
            }
            throw RestartSynchronizationException()
        }
    }

    /**
     * If we see a conflict, we try to find the nearest name that has no conflict.
     * Example:
     * Conflict with path "nfc/My_card.nfc"
     * Try "nfc/My_card_1.nfc"... Failed
     * Try "nfc/My_card_2.nfc"... Success!
     * Move My_card.nfc to My_card_2.nfc and restart synchronization
     */
    private suspend fun resolveConflict(keyPath: FlipperKeyPath) {
        warn { "Try resolve conflict with $keyPath" }
        val oldKey = simpleKeyApi.getKey(keyPath)
            ?: error("Can't found key $keyPath on Android side")
        val newPath = utilsKeyApi.findAvailablePath(keyPath)

        updateKeyApi.updateKey(
            oldKey,
            oldKey.copy(
                oldKey.mainFile.copy(path = newPath.path),
                deleted = newPath.deleted
            )
        )
    }

}