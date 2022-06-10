package com.flipperdevices.keyscreen.impl.viewmodel

import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ui.lifecycle.LifecycleViewModel
import com.flipperdevices.keyscreen.impl.di.KeyScreenComponent
import com.flipperdevices.keyscreen.impl.model.FlipperDeviceState
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class FlipperDeviceViewModel : LifecycleViewModel(), FlipperBleServiceConsumer {
    private val flipperDeviceState = MutableStateFlow(FlipperDeviceState.NOT_CONNECTED)

    @Inject
    lateinit var serviceProvider: FlipperServiceProvider

    init {
        ComponentHolder.component<KeyScreenComponent>().inject(this)
        serviceProvider.provideServiceApi(this, this)
    }

    fun getFlipperDeviceState(): StateFlow<FlipperDeviceState> = flipperDeviceState

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) {
        serviceApi.connectionInformationApi.getConnectionStateFlow().onEach {
            val state = if (it is ConnectionState.Ready) {
                FlipperDeviceState.CONNECTED
            } else FlipperDeviceState.NOT_CONNECTED
            flipperDeviceState.emit(state)
        }.launchIn(viewModelScope)
    }
}
