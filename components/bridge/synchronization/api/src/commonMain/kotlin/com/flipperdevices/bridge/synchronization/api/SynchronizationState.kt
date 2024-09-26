package com.flipperdevices.bridge.synchronization.api

import androidx.compose.runtime.Immutable
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType

@Immutable
sealed interface SynchronizationState {
    data object NotStarted : SynchronizationState

    sealed interface InProgress : SynchronizationState {
        val progress: Float

        data class FileInProgress(
            override val progress: Float,
            val fileName: String
        ) : InProgress

        data class Default(override val progress: Float) : InProgress

        data class Prepare(override val progress: Float) : InProgress

        data class PrepareHashes(
            override val progress: Float,
            val keyType: FlipperKeyType
        ) : InProgress

        data class Favorites(override val progress: Float) : InProgress
    }

    data object Finished : SynchronizationState
}
