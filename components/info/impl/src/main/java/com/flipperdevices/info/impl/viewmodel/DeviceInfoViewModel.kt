package com.flipperdevices.info.impl.viewmodel

import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.api.model.FlipperRequestRpcInformationStatus
import com.flipperdevices.bridge.api.model.FlipperRpcInformation
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.ui.lifecycle.LifecycleViewModel
import com.flipperdevices.info.impl.model.DeviceInfo
import com.flipperdevices.info.impl.model.DeviceInfoRequestStatus
import com.flipperdevices.updater.api.FlipperVersionProviderApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import tangle.viewmodel.VMInject

class DeviceInfoViewModel @VMInject constructor(
    serviceProvider: FlipperServiceProvider,
    private val flipperVersionProviderApi: FlipperVersionProviderApi
) : LifecycleViewModel(),
    FlipperBleServiceConsumer,
    LogTagProvider {
    override val TAG = "DeviceInfoViewModel"
    private val deviceInfoState = MutableStateFlow(DeviceInfo())
    private val flipperRpcInformationState = MutableStateFlow(FlipperRpcInformation())
    private val deviceInfoRequestStatus = MutableStateFlow(DeviceInfoRequestStatus())

    init {
        serviceProvider.provideServiceApi(this, this)
    }

    fun getDeviceInfo(): StateFlow<DeviceInfo> = deviceInfoState
    fun getDeviceInfoRequestStatus(): StateFlow<DeviceInfoRequestStatus> = deviceInfoRequestStatus

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) {
        flipperVersionProviderApi
            .getCurrentFlipperVersion(viewModelScope, serviceApi)
            .onEach { firmwareVersion ->
                deviceInfoState.update { it.copy(firmwareVersion = firmwareVersion) }
            }.launchIn(viewModelScope)

        serviceApi.flipperRpcInformationApi.getRpcInformationFlow().onEach { rpcInformation ->
            deviceInfoState.update {
                it.copy(
                    flashInt = rpcInformation.internalStorageStats,
                    flashSd = rpcInformation.externalStorageStats
                )
            }
            flipperRpcInformationState.emit(rpcInformation)
        }.launchIn(viewModelScope)

        serviceApi.flipperRpcInformationApi.getRequestRpcInformationStatus().onEach {
            info { "FlipperRequestRpcInformationStatus: $it" }
            when (it) {
                is FlipperRequestRpcInformationStatus.InProgress ->
                    deviceInfoRequestStatus.emit(DeviceInfoRequestStatus(it))
                FlipperRequestRpcInformationStatus.NotStarted ->
                    deviceInfoRequestStatus.emit(DeviceInfoRequestStatus())
            }
        }.launchIn(viewModelScope)
    }
}
