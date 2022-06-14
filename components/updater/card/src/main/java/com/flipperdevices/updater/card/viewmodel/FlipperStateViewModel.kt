package com.flipperdevices.updater.card.viewmodel

import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ktx.jre.combine
import com.flipperdevices.core.ui.lifecycle.LifecycleViewModel
import com.flipperdevices.updater.api.UpdaterApi
import com.flipperdevices.updater.card.di.CardComponent
import com.flipperdevices.updater.card.model.FlipperState
import com.flipperdevices.updater.model.UpdatingState
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class FlipperStateViewModel : LifecycleViewModel(), FlipperBleServiceConsumer {
    private val flipperStateFlow = MutableStateFlow<FlipperState>(FlipperState.NotReady)

    @Inject
    lateinit var serviceProvider: FlipperServiceProvider

    @Inject
    lateinit var updaterApi: UpdaterApi

    init {
        ComponentHolder.component<CardComponent>().inject(this)
        serviceProvider.provideServiceApi(consumer = this, lifecycleOwner = this)
    }

    fun getState(): StateFlow<FlipperState> = flipperStateFlow

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) {
        serviceApi.connectionInformationApi.getConnectionStateFlow()
            .combine(updaterApi.getState())
            .onEach { (connectionState, updaterState) ->
                val isReady =
                    connectionState is ConnectionState.Ready && connectionState.isSupported
                if (!isReady) {
                    flipperStateFlow.emit(FlipperState.NotReady)
                    return@onEach
                }
                if (updaterState.state is UpdatingState.Rebooting) {
                    flipperStateFlow.emit(FlipperState.Updating)
                } else flipperStateFlow.emit(FlipperState.Ready)
            }.launchIn(viewModelScope)
    }
}
