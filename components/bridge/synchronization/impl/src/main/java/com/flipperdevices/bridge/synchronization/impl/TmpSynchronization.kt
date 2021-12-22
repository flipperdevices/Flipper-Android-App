package com.flipperdevices.bridge.synchronization.impl

import com.flipperdevices.bridge.dao.api.DaoApi
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.bridge.synchronization.impl.di.SynchronizationComponent
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class TmpSynchronization : LogTagProvider {
    override val TAG = "TestSynchronization"

    private val isLaunched = AtomicBoolean(false)
    private val synchronizationState = MutableStateFlow(SynchronizationState.NOT_STARTED)

    @Inject
    lateinit var serviceProvider: FlipperServiceProvider

    @Inject
    lateinit var daoApi: DaoApi

    init {
        ComponentHolder.component<SynchronizationComponent>().inject(this)
    }

    fun requestServiceAndReceive() {
        if (!isLaunched.compareAndSet(false, true)) {
            info { "Synchronization skipped, because we already in synchronization" }
            return
        }
        val synchronizationTask = SynchronizationTask(serviceProvider, daoApi)
        synchronizationTask.start { taskState ->
            synchronizationState.update { taskState }
            if (taskState == SynchronizationState.FINISHED) {
                isLaunched.compareAndSet(true, false)
            }
        }
    }

    fun getSynchronizationState(): StateFlow<SynchronizationState> {
        return synchronizationState
    }
}
