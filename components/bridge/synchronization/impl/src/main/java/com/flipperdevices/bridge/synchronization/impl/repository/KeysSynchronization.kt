package com.flipperdevices.bridge.synchronization.impl.repository

import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.bridge.synchronization.impl.di.TaskGraph
import com.flipperdevices.bridge.synchronization.impl.repository.flipper.TimestampSynchronizationChecker
import com.flipperdevices.bridge.synchronization.impl.repository.manifest.ManifestTimestampRepository
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

interface KeysSynchronization {
    suspend fun syncKeys(
        onStateUpdate: suspend (SynchronizationState) -> Unit
    )
}

@ContributesBinding(TaskGraph::class, KeysSynchronization::class)
class KeysSynchronizationImpl @Inject constructor(
    private val folderKeySynchronization: FolderKeySynchronization,
    private val timestampSynchronizationChecker: TimestampSynchronizationChecker,
    private val manifestTimestampRepository: ManifestTimestampRepository
) : KeysSynchronization, LogTagProvider {
    override val TAG = "KeysSynchronization"

    override suspend fun syncKeys(
        onStateUpdate: suspend (SynchronizationState) -> Unit
    ) {
        timestampSynchronizationChecker.fetchFoldersTimestamp(
            FlipperKeyType.values()
        ) { type, timestamp ->
            if (timestamp == null ||
                manifestTimestampRepository.isUpdateRequired(type, timestamp)
            ) {
                folderKeySynchronization.syncFolder(type)
                if (timestamp != null) {
                    manifestTimestampRepository.setTimestampForType(type, timestamp)
                }
            } else info { "Skip update $type" }
        }
    }
}
