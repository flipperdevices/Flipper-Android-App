package com.flipperdevices.info.impl.viewmodel

import androidx.datastore.core.DataStore
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.navigation.global.CiceroneGlobal
import com.flipperdevices.core.preference.pb.PairSettings
import com.flipperdevices.core.ui.lifecycle.LifecycleViewModel
import com.flipperdevices.firstpair.api.FirstPairApi
import com.flipperdevices.info.impl.di.InfoComponent
import com.flipperdevices.info.impl.model.ConnectRequestState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

class ConnectViewModel : LifecycleViewModel() {
    private val connectRequestState = MutableStateFlow(
        ConnectRequestState.NOT_REQUESTED
    )
    private val alreadyRequestConnect = AtomicBoolean(false)

    @Inject
    lateinit var firstPairApi: FirstPairApi

    @Inject
    lateinit var serviceProvider: FlipperServiceProvider

    @Inject
    lateinit var synchronizationApi: SynchronizationApi

    @Inject
    lateinit var dataStoreFirstPair: DataStore<PairSettings>

    @Inject
    lateinit var ciceroneGlobal: CiceroneGlobal

    init {
        ComponentHolder.component<InfoComponent>().inject(this)
    }

    fun goToConnectScreen() {
        ciceroneGlobal.getRouter().navigateTo(firstPairApi.getFirstPairScreen())
    }

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
