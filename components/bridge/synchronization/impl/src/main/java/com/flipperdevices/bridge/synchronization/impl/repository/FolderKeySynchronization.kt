package com.flipperdevices.bridge.synchronization.impl.repository

import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.bridge.synchronization.impl.di.TaskGraph
import com.flipperdevices.bridge.synchronization.impl.model.DiffSource
import com.flipperdevices.bridge.synchronization.impl.repository.android.AndroidHashRepository
import com.flipperdevices.bridge.synchronization.impl.repository.android.SynchronizationStateRepository
import com.flipperdevices.bridge.synchronization.impl.repository.flipper.FlipperHashRepository
import com.flipperdevices.bridge.synchronization.impl.repository.manifest.ManifestRepository
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

interface FolderKeySynchronization {
    suspend fun syncFolder(flipperKeyType: FlipperKeyType)
}

@ContributesBinding(TaskGraph::class, FolderKeySynchronization::class)
class FolderKeySynchronizationImpl @Inject constructor(
    private val androidHashRepository: AndroidHashRepository,
    private val flipperHashRepository: FlipperHashRepository,
    private val manifestRepository: ManifestRepository,
    private val synchronizationRepository: SynchronizationStateRepository,
    private val simpleKeyApi: SimpleKeyApi,
    private val keyDiffApplier: KeyDiffApplier
) : FolderKeySynchronization, LogTagProvider {
    override val TAG = "FolderKeySynchronization"

    override suspend fun syncFolder(flipperKeyType: FlipperKeyType) {
        info { "Start synchronize $flipperKeyType" }
        val androidKeys = simpleKeyApi.getExistKeys(flipperKeyType)
        val androidHashes = androidHashRepository.getHashes(androidKeys)
        info { "Finish receive hashes from Android for $flipperKeyType: $androidHashes" }
        val flipperHashes = flipperHashRepository.getHashesForType(flipperKeyType)
        info { "Finish receive hashes from Flipper for $flipperKeyType: $flipperHashes" }

        val diffWithAndroid = manifestRepository.compareFolderKeysWithManifest(
            flipperKeyType = flipperKeyType,
            keys = androidHashes,
            diffSource = DiffSource.ANDROID
        )
        info { "Diff with manifest for android $flipperKeyType: $diffWithAndroid" }
        val diffWithFlipper = manifestRepository.compareFolderKeysWithManifest(
            flipperKeyType = flipperKeyType,
            keys = androidHashes,
            diffSource = DiffSource.FLIPPER
        )
        info { "Diff with manifest for flipper $flipperKeyType: $diffWithFlipper" }

        keyDiffApplier.applyDiffs(diffWithFlipper, diffWithAndroid)

        synchronizationRepository.markAsSynchronized(androidKeys)
    }
}