package com.flipperdevices.bridge.synchronization.impl.repository

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.dao.api.delegates.KeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.synchronization.impl.executor.AndroidKeyStorage
import com.flipperdevices.bridge.synchronization.impl.executor.DiffKeyExecutor
import com.flipperdevices.bridge.synchronization.impl.executor.FlipperKeyStorage
import com.flipperdevices.bridge.synchronization.impl.model.DiffSource
import com.flipperdevices.bridge.synchronization.impl.model.KeyDiff
import com.flipperdevices.bridge.synchronization.impl.model.KeyWithHash
import com.flipperdevices.bridge.synchronization.impl.model.RestartSynchronizationException
import com.flipperdevices.bridge.synchronization.impl.model.trackProgressAndReturn
import com.flipperdevices.bridge.synchronization.impl.repository.android.AndroidHashRepository
import com.flipperdevices.bridge.synchronization.impl.repository.flipper.FlipperHashRepository
import com.flipperdevices.bridge.synchronization.impl.repository.flipper.KeysListingRepository
import com.flipperdevices.bridge.synchronization.impl.repository.storage.ManifestRepository
import com.flipperdevices.bridge.synchronization.impl.utils.KeyDiffCombiner
import com.flipperdevices.bridge.synchronization.impl.utils.UnresolvedConflictException
import com.flipperdevices.core.log.BuildConfig
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.log.warn
import com.flipperdevices.shake2report.api.Shake2ReportApi

class KeysSynchronization(
    private val keysApi: KeyApi,
    private val manifestRepository: ManifestRepository,
    private val flipperStorage: FlipperKeyStorage,
    private val requestApi: FlipperRequestApi,
    reportApi: Shake2ReportApi
) : LogTagProvider {
    override val TAG = "KeysSynchronization"

    private val diffKeyExecutor = DiffKeyExecutor(reportApi)
    private val keysListingRepository = KeysListingRepository()
    private val flipperHashRepository = FlipperHashRepository()
    private val androidHashRepository = AndroidHashRepository()
    private val androidStorage = AndroidKeyStorage(keysApi)

    suspend fun syncKeys(): List<KeyWithHash> {
        val keysFromAndroid = keysApi.getAllKeys()
        val keysFromFlipper = keysListingRepository
            .getAllKeys(requestApi)
            .trackProgressAndReturn {
                info { "[Keys] Progress is ${it.currentPosition}/${it.maxPosition}: ${it.text}" }
            }
        val hashesFromAndroid = androidHashRepository.calculateHash(keysFromAndroid)
        val hashesFromFlipper = flipperHashRepository
            .calculateHash(requestApi, keysFromFlipper)
            .trackProgressAndReturn {
                info { "[Hash] Progress is ${it.currentPosition}/${it.maxPosition}: ${it.text}" }
            }

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
            "[Keys] Android, successful applied" +
                " ${appliedKeysToFlipper.size} from ${diffForFlipper.size} changes"
        }

        // Apply changes for Android
        val appliedKeysToAndroid = diffKeyExecutor.executeBatch(
            source = flipperStorage,
            target = androidStorage,
            diffForAndroid
        )

        info {
            "[Keys] Flipper, successful applied " +
                "${appliedKeysToAndroid.size} from ${diffForAndroid.size} changes"
        }

        // The state of the files should be the same, but it is cheaper to calculate it on Android
        val manifestHashes = androidHashRepository.calculateHash(keysApi.getAllKeys())

        if (BuildConfig.DEBUG) { // If we launch in debug mode, we check hash also on flipper
            checkManifestOnFlipper(manifestHashes)
        }

        return manifestHashes
    }

    private suspend fun checkManifestOnFlipper(manifestOnAndroid: List<KeyWithHash>) {
        val keysFromFlipper = keysListingRepository.getAllKeys(requestApi).trackProgressAndReturn {
            info { "[Hash] Progress is ${it.currentPosition}/${it.maxPosition}: ${it.text}" }
        }
        val hashesOnFlipperSorted = flipperHashRepository.calculateHash(requestApi, keysFromFlipper)
            .trackProgressAndReturn {
                info { "[Hash] Progress is ${it.currentPosition}/${it.maxPosition}: ${it.text}" }
            }.sortedBy { it.keyPath.pathToKey }
        val androidKeysSorted = manifestOnAndroid.sortedBy { it.keyPath.pathToKey }

        check(hashesOnFlipperSorted == manifestOnAndroid.sortedBy { it.keyPath.pathToKey }) {
            "Hashes on Flipper and on Android should be equals, but we have diff. " +
                "Flipper keys: $hashesOnFlipperSorted. Android keys: $androidKeysSorted"
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
        val oldKey = keysApi.getKey(keyPath) ?: error("Can't found key $keyPath on Android side")

        var newNameWithoutExtension = oldKey.path.nameWithoutExtension
        var newPath = getKeyPathWithDifferentNameWithoutExtension(
            oldKey.path,
            newNameWithoutExtension
        )
        var index = 1
        info {
            "Start finding free name for path $newPath " +
                "(newNameWithoutExtension=$newNameWithoutExtension)"
        }
        // Find empty key name
        while (keysApi.getKey(newPath) != null) {
            newNameWithoutExtension = "${newNameWithoutExtension}_${index++}"
            newPath = getKeyPathWithDifferentNameWithoutExtension(
                oldKey.path,
                newNameWithoutExtension
            )
            info {
                "Try $newPath ($newNameWithoutExtension)"
            }
        }
        info { "Found free key name! $newPath" }

        keysApi.insertKey(FlipperKey(newPath, oldKey.keyContent))
        keysApi.markDeleted(oldKey.path)
    }
}

private fun getKeyPathWithDifferentNameWithoutExtension(
    keyPath: FlipperKeyPath,
    nameWithoutExtension: String
): FlipperKeyPath {
    return FlipperKeyPath(
        keyPath.folder,
        "$nameWithoutExtension.${keyPath.name.substringAfterLast('.')}"
    )
}
