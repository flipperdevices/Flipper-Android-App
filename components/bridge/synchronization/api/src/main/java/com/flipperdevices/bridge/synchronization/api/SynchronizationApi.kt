package com.flipperdevices.bridge.synchronization.api

import kotlinx.coroutines.flow.StateFlow

interface SynchronizationApi {
    fun startSynchronization(force: Boolean = false)

    fun getSynchronizationState(): StateFlow<SynchronizationState>
}
