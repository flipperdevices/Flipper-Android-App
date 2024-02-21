package com.flipperdevices.info.impl.viewmodel

import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.core.preference.pb.PairSettings
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.info.impl.model.ConnectRequestState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

class ConnectViewModel @Inject constructor(
    private val serviceProvider: FlipperServiceProvider,
    private val synchronizationApi: SynchronizationApi,
    private val dataStoreFirstPair: DataStore<PairSettings>
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
        serviceProvider.provideServiceApi(this) {
            viewModelScope.launch {
                it.reconnect()
                synchronizationApi.startSynchronization(force = true)
                connectRequestState.update { ConnectRequestState.NOT_REQUESTED }
                alreadyRequestConnect.compareAndSet(true, false)
            }
        }
    }

    fun onDisconnect() {
        serviceProvider.provideServiceApi(this) {
            viewModelScope.launch {
                it.disconnect()
            }
        }
    }

    fun requestSynchronize() {
        synchronizationApi.startSynchronization(force = true)
    }

    fun forgetFlipper() {
        viewModelScope.launch {
            dataStoreFirstPair.updateData {
                it.toBuilder()
                    .clearDeviceName()
                    .clearDeviceId()
                    .build()
            }
        }
    }
}
