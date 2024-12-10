package com.flipperdevices.info.impl.viewmodel

import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
import com.flipperdevices.bridge.connection.feature.getinfo.api.FGetInfoFeatureApi
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureStatus
import com.flipperdevices.bridge.connection.feature.provider.api.get
import com.flipperdevices.bridge.connection.orchestrator.api.FDeviceOrchestrator
import com.flipperdevices.bridge.connection.orchestrator.api.model.FDeviceConnectStatus
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.preference.pb.PairSettings
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.info.impl.model.DeviceStatus
import com.flipperdevices.updater.api.UpdateStateApi
import com.flipperdevices.updater.model.FlipperUpdateState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class DeviceStatusViewModel @Inject constructor(
    serviceProvider: FlipperServiceProvider,
    private val dataStorePair: DataStore<PairSettings>,
    private val updateStateApi: UpdateStateApi,
    private val fFeatureProvider: FFeatureProvider,
    private val fDeviceOrchestrator: FDeviceOrchestrator
) : DecomposeViewModel(), FlipperBleServiceConsumer {
    private val updateStatus = MutableStateFlow<FlipperUpdateState>(FlipperUpdateState.NotConnected)

    init {
        serviceProvider.provideServiceApi(consumer = this, lifecycleOwner = this)
    }
    fun getState(): StateFlow<DeviceStatus> = combine(
        flow = fDeviceOrchestrator.getState(),
        flow2 = fFeatureProvider.get<FGetInfoFeatureApi>()
            .map { status -> status as? FFeatureStatus.Supported<FGetInfoFeatureApi> }
            .flatMapLatest { status -> status?.featureApi?.getGattInfoFlow() ?: emptyFlow() },
        flow3 = dataStorePair.data
    ) { connectionState, flipperInformation, pairSettings ->
        when (connectionState) {
            is FDeviceConnectStatus.Disconnected -> {
                if (pairSettings.device_name.isBlank() || pairSettings.device_id.isBlank()) {
                    DeviceStatus.NoDevice
                } else {
                    DeviceStatus.NoDeviceInformation(
                        pairSettings.device_name,
                        connectInProgress = false
                    )
                }
            }

            is FDeviceConnectStatus.Connecting,
            is FDeviceConnectStatus.Disconnecting,
            is FDeviceConnectStatus.Connected -> {
                var deviceName = pairSettings.device_name
                if (deviceName.isBlank()) {
                    deviceName = "Unknown"
                }
                val batteryLevel = flipperInformation.batteryLevel
                if (batteryLevel == null) {
                    DeviceStatus.NoDeviceInformation(
                        deviceName,
                        connectInProgress = true
                    )
                } else {
                    DeviceStatus.Connected(
                        deviceName,
                        batteryLevel,
                        flipperInformation.isCharging
                    )
                }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, DeviceStatus.NoDevice)

    fun getUpdateState(): StateFlow<FlipperUpdateState> = updateStatus

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) {
        // todo
        updateStateApi.getFlipperUpdateState(serviceApi, viewModelScope).onEach {
            updateStatus.emit(it)
        }.launchIn(viewModelScope)
    }
}
