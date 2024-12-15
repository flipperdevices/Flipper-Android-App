package com.flipperdevices.bridge.connection.service.impl

import com.flipperdevices.bridge.connection.config.api.FDevicePersistedStorage
import com.flipperdevices.bridge.connection.orchestrator.api.FDeviceOrchestrator
import com.flipperdevices.bridge.connection.service.api.FConnectionService
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.warn
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ContributesBinding(AppGraph::class, FConnectionService::class)
class FConnectionServiceImpl @Inject constructor(
    private val orchestrator: FDeviceOrchestrator,
    private val fDevicePersistedStorage: FDevicePersistedStorage
) : FConnectionService, LogTagProvider {
    override val TAG: String = "FConnectionService"

    private val scope = CoroutineScope(SupervisorJob() + FlipperDispatchers.workStealingDispatcher)
    private val mutex = Mutex()
    private val isForceDisconnected = MutableStateFlow(false)

    override fun onApplicationInit() {
        scope.launch {
            if (mutex.isLocked) {
                warn { "#onApplicationInit tried to init connection service again" }
                return@launch
            }
            mutex.withLock {
                val connectionJob = combine(
                    flow = fDevicePersistedStorage.getCurrentDevice(),
                    flow2 = isForceDisconnected,
                    transform = { currentDevice, isForceDisconnected ->
                        when {
                            isForceDisconnected -> orchestrator.disconnectCurrent()
                            currentDevice == null -> orchestrator.disconnectCurrent()
                            else -> orchestrator.connect(currentDevice)
                        }
                    }
                ).launchIn(this)
                connectionJob.invokeOnCompletion { warn { "#onApplicationInit connection job cancelled" } }
                connectionJob.join()
            }
        }
    }

    override fun forceReconnect() {
        scope.launch { isForceDisconnected.emit(false) }
    }

    override fun disconnect(force: Boolean) {
        scope.launch {
            isForceDisconnected.emit(force)
            orchestrator.disconnectCurrent()
        }
    }
}