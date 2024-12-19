package com.flipperdevices.info.impl.viewmodel

import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.connection.feature.getinfo.api.FGattInfoFeatureApi
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureStatus
import com.flipperdevices.bridge.connection.feature.provider.api.get
import com.flipperdevices.bridge.connection.orchestrator.api.FDeviceOrchestrator
import com.flipperdevices.bridge.connection.orchestrator.api.model.FDeviceConnectStatus
import com.flipperdevices.core.preference.pb.PairSettings
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.info.impl.model.DeviceStatus
import com.flipperdevices.updater.api.FirmwareVersionBuilderApi
import com.flipperdevices.updater.api.UpdaterApi
import com.flipperdevices.updater.model.FlipperUpdateState
import com.flipperdevices.updater.model.UpdatingState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class DeviceStatusViewModel @Inject constructor(
    dataStorePair: DataStore<PairSettings>,
    fFeatureProvider: FFeatureProvider,
    fDeviceOrchestrator: FDeviceOrchestrator,
    private val updaterApi: UpdaterApi,
    private val firmwareVersionBuilderApi: FirmwareVersionBuilderApi
) : DecomposeViewModel() {

    private val deviceState = combine(
        flow = fDeviceOrchestrator.getState(),
        flow2 = fFeatureProvider.get<FGattInfoFeatureApi>()
            .map { status -> status as? FFeatureStatus.Supported<FGattInfoFeatureApi> }
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

    fun getState(): StateFlow<DeviceStatus> = deviceState

    private val updateStatus = combine(
        flow = updaterApi.getState(),
        flow2 = fFeatureProvider.get<FGattInfoFeatureApi>()
            .map { it as? FFeatureStatus.Supported<FGattInfoFeatureApi> }
            .flatMapLatest { it?.featureApi?.getGattInfoFlow() ?: flowOf(null) }
            .map { gattInfo -> gattInfo?.softwareVersion }
            .map { firmwareVersion ->
                firmwareVersion?.let(firmwareVersionBuilderApi::buildFirmwareVersionFromString)
            },
        flow3 = fDeviceOrchestrator.getState(),
        transform = { updaterState, firmwareVersion, orchestratorState ->
            when (orchestratorState) {
                is FDeviceConnectStatus.Connected -> {
                    if (firmwareVersion != null) {
                        when (updaterState.state) {
                            is UpdatingState.Rebooting -> {
                                updaterApi.onDeviceConnected(firmwareVersion)
                                FlipperUpdateState.Ready
                            }

                            is UpdatingState.Complete -> {
                                FlipperUpdateState.Complete(updaterState.request?.updateTo)
                            }

                            is UpdatingState.Failed -> {
                                FlipperUpdateState.Failed(updaterState.request?.updateTo)
                            }

                            else -> FlipperUpdateState.Ready
                        }
                    } else if (updaterState.state is UpdatingState.Rebooting) {
                        FlipperUpdateState.Updating
                    } else {
                        FlipperUpdateState.ConnectingInProgress
                    }
                }

                is FDeviceConnectStatus.Connecting -> FlipperUpdateState.ConnectingInProgress
                is FDeviceConnectStatus.Disconnecting -> FlipperUpdateState.ConnectingInProgress
                is FDeviceConnectStatus.Disconnected -> FlipperUpdateState.NotConnected
            }
        }
    ).stateIn(viewModelScope, SharingStarted.Eagerly, FlipperUpdateState.NotConnected)

    fun getUpdateState(): StateFlow<FlipperUpdateState> = updateStatus
}
