package com.flipperdevices.connection.impl.viewmodel

import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.connection.feature.protocolversion.model.FlipperSupportedState
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.orchestrator.api.FDeviceOrchestrator
import com.flipperdevices.bridge.connection.orchestrator.api.model.ConnectingStatus
import com.flipperdevices.bridge.connection.orchestrator.api.model.FDeviceConnectStatus
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.connection.impl.model.ConnectionStatusState
import com.flipperdevices.connection.impl.util.getSupportedState
import com.flipperdevices.core.preference.pb.PairSettings
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TIMEOUT_SYNCHRONIZED_STATUS_MS = 3000L

class ConnectionStatusViewModel @Inject constructor(
    private val synchronizationApi: SynchronizationApi,
    private val pairSettingsStore: DataStore<PairSettings>,
    private val featureProvider: FFeatureProvider,
    private val orchestrator: FDeviceOrchestrator
) : DecomposeViewModel() {
    private val statusState = MutableStateFlow<ConnectionStatusState>(
        ConnectionStatusState.Disconnected
    )
    private var switchFromSynchronizedJob: Job? = null

    init {
        combine(
            flow = orchestrator.getState(),
            flow2 = synchronizationApi.getSynchronizationState(),
            flow3 = featureProvider.getSupportedState(),
            transform = { deviceState, syncState, supportedState ->
                if (deviceState is FDeviceConnectStatus.Connected) {
                    syncState.toConnectionStatus()
                } else {
                    deviceState.toConnectionStatus(supportedState)
                }
            }
        ).onEach { connectionStatusState ->
            if (connectionStatusState is ConnectionStatusState.Synchronized &&
                switchFromSynchronizedJob == null
            ) {
                switchFromSynchronizedJob = viewModelScope.launch {
                    delay(TIMEOUT_SYNCHRONIZED_STATUS_MS)
                    statusState.update {
                        if (it is ConnectionStatusState.Synchronized) {
                            ConnectionStatusState.Connected
                        } else {
                            it
                        }
                    }
                    switchFromSynchronizedJob = null
                }
            } else {
                switchFromSynchronizedJob?.cancel()
                switchFromSynchronizedJob = null
            }
            statusState.emit(connectionStatusState)
        }.launchIn(viewModelScope)
    }

    @Composable
    fun getStatusState(): StateFlow<ConnectionStatusState> = statusState

    private suspend fun FDeviceConnectStatus.toConnectionStatus(supportedState: FlipperSupportedState) =
        when (this) {
            is FDeviceConnectStatus.Connecting -> {
                when (this.status) {
                    ConnectingStatus.CONNECTING -> ConnectionStatusState.Connecting
                    ConnectingStatus.INITIALIZING -> ConnectionStatusState.Connecting
                }
            }

            is FDeviceConnectStatus.Connected -> {
                if (supportedState == FlipperSupportedState.READY) {
                    ConnectionStatusState.Connected
                } else {
                    ConnectionStatusState.Unsupported
                }
            }

            is FDeviceConnectStatus.Disconnecting -> ConnectionStatusState.Connecting
            is FDeviceConnectStatus.Disconnected -> {
                if (pairSettingsStore.data.first().device_id.isBlank()) {
                    ConnectionStatusState.NoDevice
                } else {
                    ConnectionStatusState.Disconnected
                }
            }
        }
}

private fun SynchronizationState.toConnectionStatus() = when (this) {
    SynchronizationState.NotStarted -> ConnectionStatusState.Connected
    is SynchronizationState.InProgress -> ConnectionStatusState.Synchronization(progress)
    SynchronizationState.Finished -> ConnectionStatusState.Synchronized
}
