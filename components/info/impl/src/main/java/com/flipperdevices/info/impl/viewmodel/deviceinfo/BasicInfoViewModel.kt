package com.flipperdevices.info.impl.viewmodel.deviceinfo

import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
import com.flipperdevices.bridge.api.manager.ktx.state.FlipperSupportedState
import com.flipperdevices.bridge.rpcinfo.api.FlipperStorageInformationApi
import com.flipperdevices.bridge.rpcinfo.model.FlipperInformationStatus
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.ktx.jre.cancelAndClear
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.info.impl.model.FlipperBasicInfo
import com.flipperdevices.updater.api.FlipperVersionProviderApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

class BasicInfoViewModel @Inject constructor(
    serviceProvider: FlipperServiceProvider,
    private val flipperVersionProviderApi: FlipperVersionProviderApi,
    private val flipperStorageInformationApi: FlipperStorageInformationApi
) : DecomposeViewModel(), FlipperBleServiceConsumer, LogTagProvider {
    override val TAG = "DeviceInfoViewModel"
    private val flipperBasicInfoState = MutableStateFlow(FlipperBasicInfo())
    private var jobs = mutableListOf<Job>()

    init {
        serviceProvider.provideServiceApi(this, this)
    }

    fun getDeviceInfo() = flipperBasicInfoState.asStateFlow()

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) {
        jobs.cancelAndClear()
        jobs += flipperVersionProviderApi.getCurrentFlipperVersion(viewModelScope, serviceApi)
            .onEach { firmwareVersion ->
                flipperBasicInfoState.update {
                    it.copy(
                        firmwareVersion = FlipperInformationStatus.Ready(
                            firmwareVersion
                        )
                    )
                }
            }.launchIn(viewModelScope)

        jobs += serviceApi.connectionInformationApi.getConnectionStateFlow().onEach {
            when (it) {
                is ConnectionState.Ready -> if (it.supportedState == FlipperSupportedState.READY) {
                    flipperStorageInformationApi.invalidate(
                        viewModelScope,
                        serviceApi,
                        force = false
                    )
                }
                else -> flipperStorageInformationApi.reset()
            }
        }.launchIn(viewModelScope)

        jobs += flipperStorageInformationApi.getStorageInformationFlow().onEach { storageInfo ->
            flipperBasicInfoState.update { it.copy(storageInfo = storageInfo) }
        }.launchIn(viewModelScope)

        jobs += viewModelScope.launch {
            flipperStorageInformationApi.invalidate(
                viewModelScope,
                serviceApi
            )
        }
    }
}
