package com.flipperdevices.bridge.synchronization.impl.repository

import com.flipperdevices.bridge.synchronization.impl.di.TaskGraph
import com.flipperdevices.bridge.synchronization.impl.executor.AbstractKeyStorage
import com.flipperdevices.bridge.synchronization.impl.executor.DiffKeyExecutor
import com.flipperdevices.bridge.synchronization.impl.executor.Platform
import com.flipperdevices.bridge.synchronization.impl.executor.StorageType
import com.flipperdevices.bridge.synchronization.impl.model.DiffSource
import com.flipperdevices.bridge.synchronization.impl.model.KeyDiff
import com.flipperdevices.bridge.synchronization.impl.repository.manifest.DiffMergeHelper
import com.flipperdevices.core.log.info
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

interface KeyDiffApplier {
    suspend fun applyDiffs(diffWithFlipper: List<KeyDiff>, diffWithAndroid: List<KeyDiff>)
}

@ContributesBinding(TaskGraph::class, KeyDiffApplier::class)
class KeyDiffApplierImpl @Inject constructor(
    private val diffMergeHelper: DiffMergeHelper,
    private val diffKeyExecutor: DiffKeyExecutor,
    @StorageType(Platform.ANDROID)
    private val androidStorage: AbstractKeyStorage,
    @StorageType(Platform.FLIPPER)
    private val flipperStorage: AbstractKeyStorage,
) : KeyDiffApplier {
    override suspend fun applyDiffs(
        diffWithFlipper: List<KeyDiff>,
        diffWithAndroid: List<KeyDiff>
    ) {
        val mergedDiff = diffMergeHelper.mergeDiffs(diffWithFlipper, diffWithAndroid)
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