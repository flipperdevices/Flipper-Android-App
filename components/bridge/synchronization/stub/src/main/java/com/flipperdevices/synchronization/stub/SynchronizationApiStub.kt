package com.flipperdevices.synchronization.stub

import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow

@ContributesBinding(AppGraph::class)
class SynchronizationApiStub @Inject constructor() : SynchronizationApi {
    override fun startSynchronization(force: Boolean) = Unit
    override fun getSynchronizationState() = MutableStateFlow(SynchronizationState.NotStarted)
    override fun isSynchronizationRunning() = false
    override suspend fun stop() = Unit
}
