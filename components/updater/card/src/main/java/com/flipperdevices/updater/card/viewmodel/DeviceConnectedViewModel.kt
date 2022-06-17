package com.flipperdevices.updater.card.viewmodel

import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.core.ui.lifecycle.LifecycleViewModel
import com.flipperdevices.updater.card.model.DeviceConnected
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class DeviceConnectedViewModel : LifecycleViewModel(), FlipperBleServiceConsumer {
    private val deviceConnectedFlow = MutableStateFlow(DeviceConnected.CONNECTING)

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) {
        combine(
            serviceApi.connectionInformationApi.getConnectionStateFlow(),
            serviceApi.flipperInformationApi.getInformationFlow(),
        ) { connectionState, flipperInformation ->
            return@combine when (connectionState) {
                ConnectionState.Connecting,
                ConnectionState.Disconnecting,
                ConnectionState.Initializing,
                is ConnectionState.Ready,
                ConnectionState.RetrievingInformation -> {
                    if (flipperInformation.batteryLevel == null) DeviceConnected.CONNECTING
                    else DeviceConnected.NOT_CONNECTING
                }
                is ConnectionState.Disconnected -> DeviceConnected.NOT_CONNECTING
            }
        }.onEach {
            deviceConnectedFlow.emit(it)
        }.launchIn(viewModelScope)
    }

    fun getDeviceConnected(): StateFlow<DeviceConnected> = deviceConnectedFlow
}
