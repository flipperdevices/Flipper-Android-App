package com.flipperdevices.keyscreen.impl.viewmodel

import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
import com.flipperdevices.bridge.api.manager.ktx.state.FlipperSupportedState
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ui.lifecycle.LifecycleViewModel
import com.flipperdevices.keyscreen.impl.di.KeyScreenComponent
import com.flipperdevices.keyscreen.impl.model.EmulateButtonState
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class EmulateViewModel : LifecycleViewModel(), FlipperBleServiceConsumer {
    private val emulateButtonStateFlow = MutableStateFlow(EmulateButtonState.DISABLED)

    @Inject
    lateinit var serviceProvider: FlipperServiceProvider

    init {
        ComponentHolder.component<KeyScreenComponent>().inject(this)
        serviceProvider.provideServiceApi(this, this)
    }

    fun getEmulateButtonStateFlow(): StateFlow<EmulateButtonState> = emulateButtonStateFlow

    fun onStartEmulate() {
        emulateButtonStateFlow.update { EmulateButtonState.ACTIVE }
        /*serviceProvider.provideServiceApi(this) {
            it.requestApi.request()
        }*/
    }

    fun onStopEmulate() {
        emulateButtonStateFlow.update { EmulateButtonState.INACTIVE }
    }

    fun onSinglePress() {
    }

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) {
        serviceApi.connectionInformationApi.getConnectionStateFlow().onEach {
            val buttonEnabled = (it is ConnectionState.Ready) &&
                it.supportedState == FlipperSupportedState.READY

            emulateButtonStateFlow.update { emulateButtonState ->
                if (buttonEnabled) {
                    if (emulateButtonState == EmulateButtonState.DISABLED) {
                        EmulateButtonState.INACTIVE
                    } else emulateButtonState
                } else {
                    EmulateButtonState.DISABLED
                }
            }
        }.launchIn(viewModelScope)
    }
}
