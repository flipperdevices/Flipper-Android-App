package com.flipperdevices.info.impl.viewmodel

import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ui.LifecycleViewModel
import com.flipperdevices.info.impl.di.InfoComponent
import com.flipperdevices.info.impl.model.FirmwareUpdateStatus
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class FirmwareUpdateViewModel : LifecycleViewModel(), FlipperBleServiceConsumer {
    private val connectionState = MutableStateFlow<FirmwareUpdateStatus>(
        FirmwareUpdateStatus.UpToDate
    )

    @Inject
    lateinit var serviceProvider: FlipperServiceProvider

    init {
        ComponentHolder.component<InfoComponent>().inject(this)
        serviceProvider.provideServiceApi(consumer = this, lifecycleOwner = this)
    }

    fun getState(): StateFlow<FirmwareUpdateStatus> = connectionState

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) {
        serviceApi.connectionInformationApi.getConnectionStateFlow().onEach {
            if (it is ConnectionState.Ready && !it.isSupported) {
                connectionState.emit(FirmwareUpdateStatus.Unsupported)
            }
        }.launchIn(viewModelScope)
    }
}
