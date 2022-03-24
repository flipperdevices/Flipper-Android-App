package com.flipperdevices.info.impl.viewmodel

import androidx.datastore.core.DataStore
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.preference.pb.PairSettings
import com.flipperdevices.core.ui.LifecycleViewModel
import com.flipperdevices.info.impl.di.InfoComponent
import com.flipperdevices.info.impl.model.DeviceStatus
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class DeviceViewModel : LifecycleViewModel(), FlipperBleServiceConsumer {
    private val deviceStatus = MutableStateFlow<DeviceStatus>(DeviceStatus.NoDevice)

    @Inject
    lateinit var serviceProvider: FlipperServiceProvider

    @Inject
    lateinit var dataStorePair: DataStore<PairSettings>

    init {
        ComponentHolder.component<InfoComponent>().inject(this)
        serviceProvider.provideServiceApi(consumer = this, lifecycleOwner = this)
    }

    fun getState(): StateFlow<DeviceStatus> = deviceStatus

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) {
        combine(
            serviceApi.connectionInformationApi.getConnectionStateFlow(),
            serviceApi.flipperInformationApi.getInformationFlow(),
            dataStorePair.data
        ) { connectionState, flipperInformation, pairSettings ->
            return@combine when (connectionState) {
                is ConnectionState.Disconnected -> if (pairSettings.deviceName == null) {
                    DeviceStatus.NoDevice
                } else DeviceStatus.NotConnected(pairSettings.deviceName)
                ConnectionState.Connecting,
                ConnectionState.Disconnecting,
                ConnectionState.Initializing,
                is ConnectionState.Ready,
                ConnectionState.RetrievingInformation -> {
                    val deviceName = pairSettings.deviceName ?: "Unknown"
                    val batteryLevel = flipperInformation.batteryLevel
                    if (batteryLevel == null) {
                        DeviceStatus.NotConnected(
                            deviceName
                        )
                    } else DeviceStatus.Connected(
                        deviceName,
                        batteryLevel
                    )
                }
            }
        }.onEach {
            deviceStatus.emit(it)
        }.launchIn(viewModelScope)
    }
}
