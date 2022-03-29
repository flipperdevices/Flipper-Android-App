package com.flipperdevices.bridge.synchronization.impl.repository.android

import com.flipperdevices.bridge.dao.api.delegates.key.UtilsKeyApi
import com.flipperdevices.bridge.synchronization.impl.model.KeyAction
import com.flipperdevices.bridge.synchronization.impl.model.KeyDiff
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error

class SynchronizationStateRepository(
    private val utilsKeyApi: UtilsKeyApi
) : LogTagProvider {
    override val TAG = "SynchronizationStateRepository"

    suspend fun markAsSynchronized(diff: List<KeyDiff>) {
        val uniqueDiffs = diff
            .distinctBy { it.newHash.keyPath }
        for (diffToMark in uniqueDiffs) {
            try {
                utilsKeyApi.markAsSynchronized(
                    diffToMark.newHash.keyPath,
                    deleted = diffToMark.action == KeyAction.DELETED
                )
            } catch (
                @Suppress("TooGenericExceptionCaught")
                exception: Exception
            ) {
                error(exception) { "While mark synchronized $diffToMark" }
            }
        }
    }
}
