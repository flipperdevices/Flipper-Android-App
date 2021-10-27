package com.flipperdevices.info.main.viewmodel

import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.api.model.FlipperGATTInformation
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.view.LifecycleViewModel
import com.flipperdevices.info.di.InfoComponent
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import no.nordicsemi.android.ble.ktx.state.ConnectionState

class InfoViewModel : LifecycleViewModel(), FlipperBleServiceConsumer {
    @Inject
    lateinit var bleService: FlipperServiceProvider

    private val informationState = MutableStateFlow(FlipperGATTInformation())
    private val connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnecting)

    init {
        ComponentHolder.component<InfoComponent>().inject(this)
        bleService.provideServiceApi(consumer = this, lifecycleOwner = this)
    }

    fun getDeviceInformation(): StateFlow<FlipperGATTInformation> {
        return informationState
    }

    fun getConnectionState(): StateFlow<ConnectionState> {
        return connectionState
    }

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) {
        serviceApi.flipperInformationApi.getInformationFlow().onEach {
            informationState.emit(it)
        }.launchIn(viewModelScope)
        serviceApi.connectionInformationApi.getConnectionStateFlow().onEach {
            connectionState.emit(it)
        }.launchIn(viewModelScope)
    }
}
