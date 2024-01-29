package com.flipperdevices.info.impl.viewmodel

import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
import com.flipperdevices.bridge.api.manager.ktx.state.FlipperSupportedState
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class FirmwareUpdateViewModel @Inject constructor(
    serviceProvider: FlipperServiceProvider
) : DecomposeViewModel(), FlipperBleServiceConsumer, LogTagProvider {
    override val TAG = "FirmwareUpdateViewModel"

    private val connectionState = MutableStateFlow(
        FlipperSupportedState.READY
    )

    init {
        serviceProvider.provideServiceApi(consumer = this, lifecycleOwner = this)
    }

    fun getState(): StateFlow<FlipperSupportedState> = connectionState

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) {
        serviceApi.connectionInformationApi.getConnectionStateFlow().onEach {
            info { "Receive connection state: $it" }
            if (it is ConnectionState.Ready) {
                connectionState.emit(it.supportedState)
            }
        }.launchIn(viewModelScope)
    }
}
