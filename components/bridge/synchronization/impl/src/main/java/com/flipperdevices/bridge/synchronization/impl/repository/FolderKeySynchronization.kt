package com.flipperdevices.bridge.synchronization.impl.repository

import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.bridge.synchronization.impl.di.TaskGraph
import com.flipperdevices.bridge.synchronization.impl.executor.AbstractKeyStorage
import com.flipperdevices.bridge.synchronization.impl.executor.DiffKeyExecutor
import com.flipperdevices.bridge.synchronization.impl.executor.Platform
import com.flipperdevices.bridge.synchronization.impl.executor.StorageType
import com.flipperdevices.bridge.synchronization.impl.model.DiffSource
import com.flipperdevices.bridge.synchronization.impl.model.KeyDiff
import com.flipperdevices.bridge.synchronization.impl.repository.android.AndroidHashRepository
import com.flipperdevices.bridge.synchronization.impl.repository.android.SynchronizationStateRepository
import com.flipperdevices.bridge.synchronization.impl.repository.flipper.FlipperHashRepository
import com.flipperdevices.bridge.synchronization.impl.repository.manifest.DiffMergeHelper
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
    private val diffMergeHelper: DiffMergeHelper,
    private val diffKeyExecutor: DiffKeyExecutor,
    @StorageType(Platform.ANDROID)
    private val androidStorage: AbstractKeyStorage,
    @StorageType(Platform.FLIPPER)
    private val flipperStorage: AbstractKeyStorage,
    private val synchronizationRepository: SynchronizationStateRepository,
    private val simpleKeyApi: SimpleKeyApi
) : FolderKeySynchronization, LogTagProvider {
    override val TAG = "FolderKeySynchronization"

    override suspend fun syncFolder(flipperKeyType: FlipperKeyType) {
        val androidKeys = simpleKeyApi.getExistKeys(flipperKeyType)
        val androidHashes = androidHashRepository.getHashes(androidKeys)
        info { "Finish receive hashes from Android for $flipperKeyType: $androidHashes" }
        val flipperHashes = flipperHashRepository.getHashesForFolder(flipperKeyType)
        info { "Finish receive hashes from Flipper for $flipperKeyType: $flipperHashes" }

        val androidDiff = manifestRepository.compareFolderKeysWithManifest(
            type = flipperKeyType,
            keys = androidHashes,
            diffSource = DiffSource.ANDROID
        )
        info { "Diff with manifest for android $flipperKeyType: $androidDiff" }
        val flipperDiff = manifestRepository.compareFolderKeysWithManifest(
            type = flipperKeyType,
            keys = androidHashes,
            diffSource = DiffSource.ANDROID
        )
        info { "Diff with manifest for flipper $flipperKeyType: $flipperDiff" }

        applyDiffs(flipperDiff, androidDiff)

        synchronizationRepository.markAsSynchronized(androidKeys)
    }

    private suspend fun applyDiffs(flipperDiff: List<KeyDiff>, androidDiff: List<KeyDiff>) {
        val mergedDiff = diffMergeHelper.mergeDiffs(flipperDiff, androidDiff)
        val diffForFlipper = mergedDiff.filter { it.source == DiffSource.ANDROID }
        val diffForAndroid = mergedDiff.filter { it.source == DiffSource.FLIPPER }

        info { "Changes for flipper $diffForFlipper" }
        info { "Changes for android $diffForAndroid" }

        // Apply changes for Flipper
        val appliedKeysToFlipper = diffKeyExecutor.executeBatch(
            source = androidStorage,
            target = flipperStorage,
            diffForFlipper
        ) { processed, total ->
            // Progress
        }

        info {
            "[Keys] Flipper, successful applied" +
                    " ${appliedKeysToFlipper.size} from ${diffForFlipper.size} changes"
        }

        // Apply changes for Android
        val appliedKeysToAndroid = diffKeyExecutor.executeBatch(
            source = flipperStorage,
            target = androidStorage,
            diffForAndroid
        ) { processed, total ->
            // Progress
        }

        info {
            "[Keys] Android, successful applied " +
                    "${appliedKeysToAndroid.size} from ${diffForAndroid.size} changes"
        }
    }
}