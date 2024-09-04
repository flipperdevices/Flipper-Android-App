package com.flipperdevices.bridge.synchronization.api

import kotlinx.coroutines.flow.StateFlow

interface SynchronizationApi {
    /**
     * @param force if true marks the synchronization dirty
     * and starts the second synchronization after the first synchronization is finished.
     */
    fun startSynchronization(force: Boolean = false)

    fun getSynchronizationState(): StateFlow<SynchronizationState>

    fun isSynchronizationRunning(): Boolean

    suspend fun stop()
}
