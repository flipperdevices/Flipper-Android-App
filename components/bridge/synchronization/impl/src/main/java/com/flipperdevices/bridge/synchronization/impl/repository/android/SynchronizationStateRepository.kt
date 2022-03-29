package com.flipperdevices.bridge.synchronization.impl.repository.android

import com.flipperdevices.bridge.dao.api.delegates.KeyApi
import com.flipperdevices.bridge.synchronization.impl.model.KeyAction
import com.flipperdevices.bridge.synchronization.impl.model.KeyDiff
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error

class SynchronizationStateRepository(
    private val keyApi: KeyApi
) : LogTagProvider {
    override val TAG = "SynchronizationStateRepository"

    suspend fun markAsSynchronized(diff: List<KeyDiff>) {
        val uniqueDiffs = diff
            .distinctBy { it.newHash.keyPath }
        for (diffToMark in uniqueDiffs) {
            try {
                keyApi.markAsSynchronized(
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
