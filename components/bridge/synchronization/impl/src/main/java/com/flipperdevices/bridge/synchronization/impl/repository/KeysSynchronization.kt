package com.flipperdevices.bridge.synchronization.impl.repository

import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.bridge.synchronization.impl.di.TaskGraph
import com.flipperdevices.bridge.synchronization.impl.repository.flipper.TimestampSynchronizationChecker
import com.flipperdevices.bridge.synchronization.impl.repository.manifest.ManifestTimestampRepository
import com.flipperdevices.bridge.synchronization.impl.utils.ProgressWrapperTracker
import com.flipperdevices.core.log.LogTagProvider
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

interface KeysSynchronization {
    suspend fun syncKeys(
        progressTracker: ProgressWrapperTracker
    )
}

private const val PERCENT_FETCH_TIMESTAMP = 0.1f

@ContributesBinding(TaskGraph::class, KeysSynchronization::class)
class KeysSynchronizationImpl @Inject constructor(
    private val folderKeySynchronization: FolderKeySynchronization,
    private val timestampSynchronizationChecker: TimestampSynchronizationChecker,
    private val manifestTimestampRepository: ManifestTimestampRepository
) : KeysSynchronization, LogTagProvider {
    override val TAG = "KeysSynchronization"

    override suspend fun syncKeys(
        progressTracker: ProgressWrapperTracker
    ) {
        val typesUpdateTimestamps = timestampSynchronizationChecker.fetchFoldersTimestamp(
            FlipperKeyType.values(),
            ProgressWrapperTracker(
                min = 0f,
                max = PERCENT_FETCH_TIMESTAMP,
                progressListener = progressTracker
            )
        )

        val typesToUpdates = typesUpdateTimestamps.filter { (type, timestamp) ->
            timestamp == null || manifestTimestampRepository.isUpdateRequired(type, timestamp)
        }
        val percentForOneType = (1.0f - PERCENT_FETCH_TIMESTAMP) / typesToUpdates.size
        var currentMinPercent = PERCENT_FETCH_TIMESTAMP
        typesToUpdates.forEach { (type, timestamp) ->
            folderKeySynchronization.syncFolder(
                type,
                ProgressWrapperTracker(
                    min = currentMinPercent,
                    max = currentMinPercent + percentForOneType,
                    progressListener = progressTracker
                )
            )
            currentMinPercent += percentForOneType
            if (timestamp != null) {
                manifestTimestampRepository.setTimestampForType(type, timestamp)
            }
        }
    }
}
