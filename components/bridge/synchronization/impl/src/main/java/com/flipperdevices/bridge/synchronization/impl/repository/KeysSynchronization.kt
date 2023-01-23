package com.flipperdevices.bridge.synchronization.impl.repository

import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.bridge.synchronization.impl.di.TaskGraph
import com.flipperdevices.core.log.LogTagProvider
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

interface KeysSynchronization {
    suspend fun syncKeys(
        onStateUpdate: suspend (SynchronizationState) -> Unit
    )
}

@ContributesBinding(TaskGraph::class, KeysSynchronization::class)
class KeysSynchronizationImpl @Inject constructor(
    private val folderKeySynchronization: FolderKeySynchronization
) : KeysSynchronization, LogTagProvider {
    override val TAG = "KeysSynchronization"

    override suspend fun syncKeys(
        onStateUpdate: suspend (SynchronizationState) -> Unit
    ) {
        FlipperKeyType.values().forEach { keyType ->
            folderKeySynchronization.syncFolder(keyType)
        }
    }
}
