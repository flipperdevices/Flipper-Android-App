package com.flipperdevices.connection.impl.viewmodel

import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
import com.flipperdevices.bridge.api.manager.ktx.state.FlipperSupportedState
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class UnsupportedStateViewModel @Inject constructor(
    serviceProvider: FlipperServiceProvider
) : DecomposeViewModel(), FlipperBleServiceConsumer {
    private val unsupportedStateFlow = MutableStateFlow(FlipperSupportedState.READY)

    init {
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
