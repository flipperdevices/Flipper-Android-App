package com.flipperdevices.bridge.synchronization.api

import androidx.compose.runtime.Immutable

@Immutable
sealed class SynchronizationState {
    data object NotStarted : SynchronizationState()
    data class InProgress(val progress: Float) : SynchronizationState()
    data object Finished : SynchronizationState()
}
