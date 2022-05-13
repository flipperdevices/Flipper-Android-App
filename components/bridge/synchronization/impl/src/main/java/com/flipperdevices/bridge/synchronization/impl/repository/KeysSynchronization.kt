package com.flipperdevices.bridge.synchronization.impl.repository

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.dao.api.delegates.key.DeleteKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.UtilsKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.bridge.synchronization.impl.executor.AndroidKeyStorage
import com.flipperdevices.bridge.synchronization.impl.executor.DiffKeyExecutor
import com.flipperdevices.bridge.synchronization.impl.executor.FlipperKeyStorage
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
import com.flipperdevices.bridge.synchronization.impl.utils.UnresolvedConflictException
import com.flipperdevices.core.log.BuildConfig
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.log.warn
import kotlin.math.max

private const val PERCENT_FLIPPER_LISTING_FOLDERS = 0.07f
private const val PERCENT_FLIPPER_HASH = 0.12f
private const val PERCENT_FLIPPER_DOWNLOAD = 0.99f

@Suppress("LongParameterList")
class KeysSynchronization(
    private val simpleKeyApi: SimpleKeyApi,
    private val deleteKeyApi: DeleteKeyApi,
    private val utilsKeyApi: UtilsKeyApi,
    private val manifestRepository: ManifestRepository,
    private val flipperStorage: FlipperKeyStorage,
    private val requestApi: FlipperRequestApi
) : LogTagProvider {
    override val TAG = "KeysSynchronization"

    private val diffKeyExecutor = DiffKeyExecutor()
    private val keysListingRepository = KeysListingRepository()
    private val flipperHashRepository = FlipperHashRepository()
    private val androidHashRepository = AndroidHashRepository()
    private val androidStorage = AndroidKeyStorage(simpleKeyApi, deleteKeyApi)
    private val synchronizationRepository = SynchronizationStateRepository(utilsKeyApi)

    suspend fun syncKeys(
        onStateUpdate: suspend (SynchronizationState) -> Unit
    ): List<KeyWithHash> {
        val keysFromAndroid = simpleKeyApi.getAllKeys()
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
            onProgressUpdate(
                onStateUpdate,
                processed,
                total,
                PERCENT_FLIPPER_HASH,
                PERCENT_FLIPPER_DOWNLOAD
            )
        }

        info {
            "[Keys] Android, successful applied " +
                "${appliedKeysToAndroid.size} from ${diffForAndroid.size} changes"
        }

        synchronizationRepository.markAsSynchronized(
            appliedKeysToAndroid.plus(appliedKeysToFlipper)
        )

        val manifestHashes = checkSynchronization(
            calculatedHashOnAndroid = ManifestChangeExecutor.applyChanges(
                hashesFromAndroid, appliedKeysToAndroid
            ),
            calculatedHashOnFlipper = ManifestChangeExecutor.applyChanges(
                hashesFromFlipper, appliedKeysToFlipper
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
        val keysFromFlipper = keysListingRepository.getAllKeys(requestApi).trackProgressAndReturn {
            onProgressUpdate(
                onStateUpdate,
                it.currentPosition,
                it.maxPosition,
                0f,
                PERCENT_FLIPPER_LISTING_FOLDERS
            )
            info { "[Hash] Progress is ${it.currentPosition}/${it.maxPosition}: ${it.text}" }
        }
        return flipperHashRepository.calculateHash(requestApi, keysFromFlipper)
            .trackProgressAndReturn {
                onProgressUpdate(
                    onStateUpdate,
                    it.currentPosition,
                    it.maxPosition,
                    PERCENT_FLIPPER_LISTING_FOLDERS,
                    PERCENT_FLIPPER_HASH
                )
                info { "[Hash] Progress is ${it.currentPosition}/${it.maxPosition}: ${it.text}" }
            }
    }

    private suspend fun mergeDiffs(first: List<KeyDiff>, second: List<KeyDiff>): List<KeyDiff> {
        return try {
            KeyDiffCombiner.combineKeyDiffs(
                first, second
            )
        } catch (conflict: UnresolvedConflictException) {
            resolveConflict(conflict.path)
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

        simpleKeyApi.insertKey(FlipperKey(newPath, oldKey.keyContent, synchronized = false))
        deleteKeyApi.markDeleted(oldKey.path)
    }

    private suspend fun onProgressUpdate(
        onStateUpdate: (suspend (SynchronizationState) -> Unit)?,
        processed: Int,
        total: Int,
        previousStagePercent: Float,
        stagePercent: Float
    ) {
        val whichPercentAllowed = max(0f, stagePercent - previousStagePercent)
        val taskPercent = processed.toFloat() / total.toFloat()
        val resultPercent = taskPercent * whichPercentAllowed
        onStateUpdate?.invoke(SynchronizationState.InProgress(previousStagePercent + resultPercent))
    }
}
