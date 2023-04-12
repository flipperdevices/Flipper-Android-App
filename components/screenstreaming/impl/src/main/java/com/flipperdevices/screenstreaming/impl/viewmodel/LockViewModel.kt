package com.flipperdevices.screenstreaming.impl.viewmodel

import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.ui.lifecycle.LifecycleViewModel
import com.flipperdevices.screenstreaming.impl.model.FlipperLockState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import tangle.viewmodel.VMInject

class LockViewModel @VMInject constructor(
    serviceProvider: FlipperServiceProvider,
) : LifecycleViewModel(), FlipperBleServiceConsumer {
    private val lockStateFlow = MutableStateFlow<FlipperLockState>(FlipperLockState.NotInitialized)

    init {
        serviceProvider.provideServiceApi(this, this)
    }

    fun getLockState() = lockStateFlow.asStateFlow()

    fun onChangeLock(isWillBeLocked: Boolean) {
        lockStateFlow.update { FlipperLockState.Ready(isWillBeLocked) }
    }

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) {
        serviceApi.connectionInformationApi.getConnectionStateFlow().onEach {
            if (it is ConnectionState.Ready) {
                lockStateFlow.emit(FlipperLockState.NotSupported)
            } else {
                lockStateFlow.emit(FlipperLockState.NotInitialized)
            }
        }.launchIn(viewModelScope)
    }
}