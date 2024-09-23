package com.flipperdevices.bridge.synchronization.impl.repository

import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.bridge.synchronization.impl.di.TaskGraph
import com.flipperdevices.bridge.synchronization.impl.model.DiffSource
import com.flipperdevices.bridge.synchronization.impl.repository.android.AndroidHashRepository
import com.flipperdevices.bridge.synchronization.impl.repository.flipper.TimestampSynchronizationChecker
import com.flipperdevices.bridge.synchronization.impl.repository.manifest.ManifestRepository
import com.flipperdevices.bridge.synchronization.impl.repository.manifest.ManifestTimestampRepository
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.progress.DetailedProgressWrapperTracker
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

interface KeysSynchronization {
    /**
     * @return keys changed
     */
    suspend fun syncKeys(
        progressTracker: DetailedProgressWrapperTracker
    ): Int
}

private const val PERCENT_FETCH_TIMESTAMP = 0.1f

@ContributesBinding(TaskGraph::class, KeysSynchronization::class)
class KeysSynchronizationImpl @Inject constructor(
    private val folderKeySynchronization: FolderKeySynchronization,
    private val timestampSynchronizationChecker: TimestampSynchronizationChecker,
    private val manifestTimestampRepository: ManifestTimestampRepository,
    private val manifestRepository: ManifestRepository,
    private val androidHashRepository: AndroidHashRepository,
    private val simpleKeyApi: SimpleKeyApi
) : KeysSynchronization, LogTagProvider {
    override val TAG = "KeysSynchronization"

    override suspend fun syncKeys(
        progressTracker: DetailedProgressWrapperTracker
    ): Int {
        val typesUpdateTimestamps = timestampSynchronizationChecker.fetchFoldersTimestamp(
            FlipperKeyType.entries.toTypedArray(),
            DetailedProgressWrapperTracker(
                min = 0f,
                max = PERCENT_FETCH_TIMESTAMP,
                progressListener = progressTracker
            )
        )

        val typesToUpdates = typesUpdateTimestamps.filter { (type, timestamp) ->
            isNeedUpdate(type, timestamp)
        }
        if (typesToUpdates.isEmpty()) {
            return -1
        }
        val percentForOneType = (1.0f - PERCENT_FETCH_TIMESTAMP) / typesToUpdates.size
        var currentMinPercent = PERCENT_FETCH_TIMESTAMP
        var keysChanged = 0
        typesToUpdates.forEach { (type, timestamp) ->
            keysChanged += folderKeySynchronization.syncFolder(
                type,
                DetailedProgressWrapperTracker(
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
        return keysChanged
    }

    private suspend fun isNeedUpdate(type: FlipperKeyType, lastFolderUpdate: Long?): Boolean {
        val androidKeys = simpleKeyApi.getExistKeys(type)
        val hashes = androidHashRepository.getHashes(androidKeys)
        val changedKeysOnAndroid = manifestRepository.compareFolderKeysWithManifest(
            folder = type.flipperDir,
            keys = hashes,
            diffSource = DiffSource.ANDROID
        )

        if (changedKeysOnAndroid.isNotEmpty()) {
            return true
        }

        if (lastFolderUpdate == null) {
            return true
        }

        if (manifestTimestampRepository.isUpdateRequired(type, lastFolderUpdate)) {
            return true
        }

        return false
    }
}
