package com.flipperdevices.bridge.synchronization.impl.repository

import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.UpdateKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.UtilsKeyApi
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.bridge.synchronization.impl.BuildConfig
import com.flipperdevices.bridge.synchronization.impl.di.TaskGraph
import com.flipperdevices.bridge.synchronization.impl.executor.AbstractKeyStorage
import com.flipperdevices.bridge.synchronization.impl.executor.DiffKeyExecutor
import com.flipperdevices.bridge.synchronization.impl.executor.Platform
import com.flipperdevices.bridge.synchronization.impl.executor.StorageType
import com.flipperdevices.bridge.synchronization.impl.model.KeyWithHash
import com.flipperdevices.bridge.synchronization.impl.repository.android.AndroidHashRepository
import com.flipperdevices.bridge.synchronization.impl.repository.android.SynchronizationStateRepository
import com.flipperdevices.bridge.synchronization.impl.repository.flipper.FlipperHashRepository
import com.flipperdevices.bridge.synchronization.impl.repository.flipper.KeysListingRepository
import com.flipperdevices.bridge.synchronization.impl.repository.manifest.ManifestChangeExecutor
import com.flipperdevices.bridge.synchronization.impl.repository.manifest.ManifestRepository
import com.flipperdevices.bridge.synchronization.impl.utils.SynchronizationPercentProvider
import com.flipperdevices.core.log.LogTagProvider
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
}
