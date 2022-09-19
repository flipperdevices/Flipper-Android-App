package com.flipperdevices.bridge.synchronization.impl.repository

import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.UpdateKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.UtilsKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.bridge.synchronization.impl.BuildConfig
import com.flipperdevices.bridge.synchronization.impl.di.TaskGraph
import com.flipperdevices.bridge.synchronization.impl.executor.AbstractKeyStorage
import com.flipperdevices.bridge.synchronization.impl.executor.DiffKeyExecutor
import com.flipperdevices.bridge.synchronization.impl.executor.Platform
import com.flipperdevices.bridge.synchronization.impl.executor.StorageType
import com.flipperdevices.bridge.synchronization.impl.model.DiffSource
import com.flipperdevices.bridge.synchronization.impl.model.KeyDiff
import com.flipperdevices.bridge.synchronization.impl.model.KeyWithHash
import com.flipperdevices.bridge.synchronization.impl.model.RestartSynchronizationException
import com.flipperdevices.bridge.synchronization.impl.model.trackProgressAndReturn
import com.flipperdevices.bridge.synchronization.impl.repository.android.AndroidHashRepository
import com.flipperdevices.bridge.synchronization.impl.repository.android.SynchronizationStateRepository
import com.flipperdevices.bridge.synchronization.impl.repository.flipper.FlipperHashRepository
import com.flipperdevices.bridge.synchronization.impl.repository.flipper.KeysListingRepository
import com.flipperdevices.bridge.synchronization.impl.repository.manifest.ManifestChangeExecutor
import com.flipperdevices.bridge.synchronization.impl.repository.storage.ManifestRepository
import com.flipperdevices.bridge.synchronization.impl.utils.KeyDiffCombiner
import com.flipperdevices.bridge.synchronization.impl.utils.SynchronizationPercentProvider
import com.flipperdevices.bridge.synchronization.impl.utils.UnresolvedConflictException
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.log.warn
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

interface KeysSynchronization {
    suspend fun syncKeys(
        onStateUpdate: suspend (SynchronizationState) -> Unit
    ): List<KeyWithHash>
}

@ContributesBinding(TaskGraph::class, KeysSynchronization::class)
@Suppress("LongParameterList")
class KeysSynchronizationImpl @Inject constructor(
    private val diffKeyExecutor: DiffKeyExecutor,
    private val keysListingRepository: KeysListingRepository,
    private val flipperHashRepository: FlipperHashRepository,
    private val androidHashRepository: AndroidHashRepository,
    @StorageType(Platform.ANDROID)
    private val androidStorage: AbstractKeyStorage,
    @StorageType(Platform.FLIPPER)
    private val flipperStorage: AbstractKeyStorage,
    private val synchronizationRepository: SynchronizationStateRepository,
    private val simpleKeyApi: SimpleKeyApi,
    private val manifestRepository: ManifestRepository,
    private val synchronizationPercentProvider: SynchronizationPercentProvider,
    private val utilsKeyApi: UtilsKeyApi,
    private val updateKeyApi: UpdateKeyApi
) : KeysSynchronization, LogTagProvider {
    override val TAG = "KeysSynchronization"

    override suspend fun syncKeys(
        onStateUpdate: suspend (SynchronizationState) -> Unit
    ): List<KeyWithHash> {
        val allKeysFromAndroid = simpleKeyApi.getAllKeys(includeDeleted = true)
        val keysFromAndroid = allKeysFromAndroid.filterNot { it.deleted }
        val hashesFromAndroid = androidHashRepository.calculateHash(keysFromAndroid)
        val hashesFromFlipper = getManifestOnFlipper(onStateUpdate)
        info { "Finish receive hashes from Flipper: $hashesFromFlipper" }
        info { "Finish receive hashes from Android: $hashesFromAndroid" }

        // Compare hashes with local snapshot
        val diffWithAndroid = manifestRepository
            .compareKeysWithManifest(hashesFromAndroid, DiffSource.ANDROID)
        val diffWithFlipper = manifestRepository
            .compareKeysWithManifest(hashesFromFlipper, DiffSource.FLIPPER)

        info { "Receive diffs on Flipper: $diffWithFlipper" }
        info { "Receive diffs on Android: $diffWithAndroid" }

        val mergedDiff = mergeDiffs(diffWithFlipper, diffWithAndroid)
        val diffForFlipper = mergedDiff.filter { it.source == DiffSource.ANDROID }
        val diffForAndroid = mergedDiff.filter { it.source == DiffSource.FLIPPER }

        info { "Changes for flipper $diffForFlipper" }
        info { "Changes for android $diffForAndroid" }

        // Apply changes for Flipper
        val appliedKeysToFlipper = diffKeyExecutor.executeBatch(
            source = androidStorage,
            target = flipperStorage,
            diffForFlipper
        )

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
            onStateUpdate(
                SynchronizationState.InProgress(
                    synchronizationPercentProvider.getDownloadProgress(processed, total)
                )
            )
        }

        info {
            "[Keys] Android, successful applied " +
                "${appliedKeysToAndroid.size} from ${diffForAndroid.size} changes"
        }

        synchronizationRepository.markAsSynchronized(allKeysFromAndroid)

        val manifestHashes = checkSynchronization(
            calculatedHashOnAndroid = ManifestChangeExecutor.applyChanges(
                hashesFromAndroid,
                appliedKeysToAndroid
            ),
            calculatedHashOnFlipper = ManifestChangeExecutor.applyChanges(
                hashesFromFlipper,
                appliedKeysToFlipper
            )
        )

        return manifestHashes
    }

    /**
     * Check that the contents on both sides are identical.
     *
     * @return manifest content, which should be stored as synchronization snapshot
     */
    private suspend fun checkSynchronization(
        calculatedHashOnFlipper: List<KeyWithHash>,
        calculatedHashOnAndroid: List<KeyWithHash>
    ): List<KeyWithHash> {
        val calculatedHashOnFlipperSorted = calculatedHashOnFlipper.sortedBy {
            it.keyPath
        }
        val calculatedHashOnAndroidSorted = calculatedHashOnAndroid.sortedBy {
            it.keyPath
        }

        check(calculatedHashOnFlipperSorted == calculatedHashOnAndroidSorted) {
            "Calculated hash should be equals. " +
                "Flipper: $calculatedHashOnFlipperSorted. Android: $calculatedHashOnAndroidSorted"
        }

        if (BuildConfig.DEBUG) {
            val hashesFromFlipperSorted = getManifestOnFlipper().sortedBy { it.keyPath }

            check(calculatedHashOnFlipperSorted == hashesFromFlipperSorted) {
                "Calculated and real hash should be equal, please check it" +
                    "Calculated: $calculatedHashOnFlipperSorted. Real: $hashesFromFlipperSorted"
            }
        }

        return calculatedHashOnFlipperSorted
    }

    private suspend fun getManifestOnFlipper(
        onStateUpdate: (suspend (SynchronizationState) -> Unit)? = null
    ): List<KeyWithHash> {
        val keysFromFlipper = keysListingRepository.getAllKeys().trackProgressAndReturn {
            onStateUpdate?.invoke(
                SynchronizationState.InProgress(
                    synchronizationPercentProvider.getListingProgress(
                        it.currentPosition,
                        it.maxPosition
                    )
                )
            )
            info { "[Hash] Progress is ${it.currentPosition}/${it.maxPosition}: ${it.text}" }
        }
        return flipperHashRepository.calculateHash(keysFromFlipper)
            .trackProgressAndReturn {
                onStateUpdate?.invoke(
                    SynchronizationState.InProgress(
                        synchronizationPercentProvider.getHashProgress(
                            it.currentPosition,
                            it.maxPosition
                        )
                    )
                )
                info { "[Hash] Progress is ${it.currentPosition}/${it.maxPosition}: ${it.text}" }
            }
    }

    private suspend fun mergeDiffs(first: List<KeyDiff>, second: List<KeyDiff>): List<KeyDiff> {
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
        val oldKey =
            simpleKeyApi.getKey(keyPath) ?: error("Can't found key $keyPath on Android side")
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
