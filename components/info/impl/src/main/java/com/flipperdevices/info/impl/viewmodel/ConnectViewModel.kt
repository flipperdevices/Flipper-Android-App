package com.flipperdevices.info.impl.viewmodel

import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.connection.config.api.FDevicePersistedStorage
import com.flipperdevices.bridge.connection.orchestrator.api.FDeviceOrchestrator
import com.flipperdevices.bridge.connection.service.api.FConnectionService
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.core.preference.pb.PairSettings
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.info.impl.model.ConnectRequestState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

class ConnectViewModel @Inject constructor(
    private val synchronizationApi: SynchronizationApi,
    private val dataStoreFirstPair: DataStore<PairSettings>,
    private val fConnectionService: FConnectionService
) : DecomposeViewModel() {
    private val connectRequestState = MutableStateFlow(
        ConnectRequestState.NOT_REQUESTED
    )
    private val alreadyRequestConnect = AtomicBoolean(false)

    fun connectAndSynchronize() {
        if (!alreadyRequestConnect.compareAndSet(false, true)) {
            return
        }
        connectRequestState.update { ConnectRequestState.CONNECTING_AND_SYNCHRONIZING }
        viewModelScope.launch {
            fConnectionService.forceReconnect()
            synchronizationApi.startSynchronization(force = true)
            connectRequestState.update { ConnectRequestState.NOT_REQUESTED }
            alreadyRequestConnect.compareAndSet(true, false)
        }
    }

    fun onDisconnect() {
        viewModelScope.launch {
            fConnectionService.disconnect(true)
        }
    }

    fun requestSynchronize() {
        synchronizationApi.startSynchronization(force = true)
    }

    fun forgetFlipper() {
        viewModelScope.launch {
            fConnectionService.forgetCurrentDevice()
            dataStoreFirstPair.updateData {
                it.copy(
                    device_name = "",
                    device_id = ""
                )
            }
        }
    }
}
