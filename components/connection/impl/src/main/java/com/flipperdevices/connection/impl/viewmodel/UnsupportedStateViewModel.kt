package com.flipperdevices.connection.impl.viewmodel

import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
import com.flipperdevices.bridge.api.manager.ktx.state.FlipperSupportedState
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.connection.impl.di.ConnectionComponent
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ui.lifecycle.LifecycleViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class UnsupportedStateViewModel : LifecycleViewModel(), FlipperBleServiceConsumer {
    private val unsupportedStateFlow = MutableStateFlow(FlipperSupportedState.READY)

    @Inject
    lateinit var serviceProvider: FlipperServiceProvider

    init {
        ComponentHolder.component<ConnectionComponent>().inject(this)
        serviceProvider.provideServiceApi(this, this)
    }

    fun getUnsupportedState(): StateFlow<FlipperSupportedState> = unsupportedStateFlow

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) {
        serviceApi.connectionInformationApi.getConnectionStateFlow().onEach {
            if (it is ConnectionState.Ready) {
                unsupportedStateFlow.emit(it.supportedState)
            }
        }.launchIn(viewModelScope)
    }
}
