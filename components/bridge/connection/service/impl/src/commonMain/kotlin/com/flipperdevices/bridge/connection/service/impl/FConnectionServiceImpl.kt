package com.flipperdevices.bridge.connection.service.impl

import com.flipperdevices.bridge.connection.config.api.FDevicePersistedStorage
import com.flipperdevices.bridge.connection.orchestrator.api.FDeviceOrchestrator
import com.flipperdevices.bridge.connection.orchestrator.api.model.DisconnectStatus
import com.flipperdevices.bridge.connection.orchestrator.api.model.FDeviceConnectStatus
import com.flipperdevices.bridge.connection.service.api.FConnectionService
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.warn
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
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

    private fun getBrokenConnectionReconnectJob(scope: CoroutineScope): Job {
        return orchestrator.getState()
            .onEach {
                if (it !is FDeviceConnectStatus.Disconnected) return@onEach
                if (it.reason != DisconnectStatus.REPORTED_BY_TRANSPORT) return@onEach
                if (isForceDisconnected.first()) return@onEach
                val currentDevice = fDevicePersistedStorage.getCurrentDevice()
                    .first()
                    ?: return@onEach
                orchestrator.disconnectCurrent()
                orchestrator.connect(currentDevice)
            }.launchIn(scope)
    }

    private fun getConnectionJob(scope: CoroutineScope): Job {
        return combine(
            flow = fDevicePersistedStorage.getCurrentDevice(),
            flow2 = isForceDisconnected,
            transform = { currentDevice, isForceDisconnected ->
                when {
                    isForceDisconnected -> orchestrator.disconnectCurrent()
                    currentDevice == null -> orchestrator.disconnectCurrent()

                    else -> orchestrator.connect(currentDevice)
                }
            }
        ).launchIn(scope)
    }

    override fun onApplicationInit() {
        scope.launch {
            if (mutex.isLocked) {
                warn { "#onApplicationInit tried to init connection service again" }
                return@launch
            }
            mutex.withLock {
                val brokenConnectionReconnectJob = getBrokenConnectionReconnectJob(this)
                val connectionJob = getConnectionJob(this)
                connectionJob.join()
                brokenConnectionReconnectJob.join()
            }
        }
    }

    override fun forceReconnect() {
        scope.launch { isForceDisconnected.emit(false) }
    }

    override fun disconnect(force: Boolean) {
        scope.launch {
            isForceDisconnected.emit(force)
        }
    }

    override fun forgetCurrentDevice() {
        scope.launch {
            fDevicePersistedStorage.getCurrentDevice()
                .first()
                ?.let { currentDevice ->
                    fDevicePersistedStorage.removeDevice(currentDevice.uniqueId)
                }
        }
    }

    override fun connectIfNotForceDisconnect() {
        scope.launch {
            if (isForceDisconnected.first()) return@launch
            val currentDevice = fDevicePersistedStorage.getCurrentDevice()
                .first()
                ?: return@launch
            orchestrator.connect(currentDevice)
        }
    }
}
