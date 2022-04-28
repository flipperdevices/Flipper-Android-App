package com.flipperdevices.bridge.synchronization.api

sealed class SynchronizationState {
    object NotStarted : SynchronizationState()
    data class InProgress(val progress: Float) : SynchronizationState()
    object Finished : SynchronizationState()
}
