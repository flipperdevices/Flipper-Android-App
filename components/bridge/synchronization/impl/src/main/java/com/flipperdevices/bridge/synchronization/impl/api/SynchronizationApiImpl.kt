package com.flipperdevices.bridge.synchronization.impl.api

import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.bridge.synchronization.impl.SynchronizationTask
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.squareup.anvil.annotations.ContributesBinding
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

@Singleton
@ContributesBinding(AppGraph::class, SynchronizationApi::class)
@Suppress("LongParameterList")
class SynchronizationApiImpl @Inject constructor(
    private val synchronizationTaskBuilder: SynchronizationTask.Builder
) : SynchronizationApi, LogTagProvider {
    override val TAG = "SynchronizationApi-${hashCode()}"

    private val isLaunched = AtomicBoolean(false)
    private val synchronizationState = MutableStateFlow<SynchronizationState>(
        SynchronizationState.NotStarted
    )
    private var synchronizationTask: SynchronizationTask? = null
    private var markDirty = false

    @Synchronized
    override fun startSynchronization(force: Boolean) {
        info { "Request synchronization..." }
        if (!isLaunched.compareAndSet(false, true)) {
            if (force) {
                markDirty = true
            }
            info { "Synchronization skipped, because we already in synchronization" }
            return
        }
        markDirty = false
        synchronizationState.update { SynchronizationState.InProgress(0f) }
        val localSynchronizationTask = synchronizationTaskBuilder.build()
        info { "Create synchronization task ${localSynchronizationTask.hashCode()}" }

        localSynchronizationTask.start(input = Unit) { taskState ->
            synchronizationState.update { taskState }
            if (taskState == SynchronizationState.Finished) {
                localSynchronizationTask.onStop()
                isLaunched.compareAndSet(true, false)
                info { "Mark launched false" }
                if (markDirty) {
                    startSynchronization()
                }
            }
        }
        synchronizationTask = localSynchronizationTask
    }

    override suspend fun stop() {
        markDirty = false
        synchronizationTask?.onStop()
    }

    override fun isSynchronizationRunning() = isLaunched.get()

    override fun getSynchronizationState(): StateFlow<SynchronizationState> {
        return synchronizationState
    }
}
