package com.flipperdevices.bridge.synchronization.api

import androidx.compose.runtime.Immutable
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType

@Immutable
sealed interface SynchronizationState {
    data object NotStarted : SynchronizationState

    sealed interface InProgress : SynchronizationState {
        val progress: Float
        val speed: Long

        data class FileInProgress(
            override val progress: Float,
            override val speed: Long,
            val fileName: String
        ) : InProgress

        data class Default(
            override val progress: Float,
            override val speed: Long,
        ) : InProgress

        data class Prepare(
            override val progress: Float,
            override val speed: Long,
        ) : InProgress

        data class PrepareHashes(
            override val progress: Float,
            override val speed: Long,
            val keyType: FlipperKeyType
        ) : InProgress

        data class Favorites(
            override val progress: Float,
            override val speed: Long,
        ) : InProgress
    }

    data object Finished : SynchronizationState
}
