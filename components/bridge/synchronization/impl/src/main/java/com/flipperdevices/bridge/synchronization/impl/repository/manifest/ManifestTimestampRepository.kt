package com.flipperdevices.bridge.synchronization.impl.repository.manifest

import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.bridge.synchronization.impl.di.TaskGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

interface ManifestTimestampRepository {
    suspend fun setTimestampForType(
        keyType: FlipperKeyType,
        timestampMs: Long
    )

    suspend fun isUpdateRequired(
        keyType: FlipperKeyType,
        currentLastUpdateMs: Long
    ): Boolean
}

@ContributesBinding(TaskGraph::class, ManifestTimestampRepository::class)
class ManifestTimestampRepositoryImpl @Inject constructor(
    private val manifestStorage: ManifestStorage
) : ManifestTimestampRepository, LogTagProvider {
    override val TAG = "ManifestTimestampRepository"

    override suspend fun setTimestampForType(keyType: FlipperKeyType, timestampMs: Long) {
        manifestStorage.update {
            val changes = it.folderChanges
            it.copy(
                folderChanges = changes.copy(
                    lastChangesTimestampMap = changes
                        .lastChangesTimestampMap.plus(keyType.flipperDir to timestampMs)
                )
            )
        }
    }

    override suspend fun isUpdateRequired(
        keyType: FlipperKeyType,
        currentLastUpdateMs: Long
    ): Boolean {
        val manifestFile = manifestStorage.load() ?: return true
        val lastChangesTimestamp = manifestFile
            .folderChanges.lastChangesTimestampMap[keyType.flipperDir] ?: return true
        val updateRequired = lastChangesTimestamp != currentLastUpdateMs
        info {
            "Update required for $keyType is $updateRequired." +
                    " Current timestamp: $currentLastUpdateMs. Last saved is $lastChangesTimestamp"
        }
        return updateRequired
    }
}