package com.flipperdevices.info.impl.viewmodel.deviceinfo

import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
import com.flipperdevices.bridge.api.manager.ktx.state.FlipperSupportedState
import com.flipperdevices.bridge.rpcinfo.api.FlipperRpcInformationApi
import com.flipperdevices.bridge.rpcinfo.model.FlipperInformationStatus
import com.flipperdevices.bridge.rpcinfo.model.FlipperRpcInformation
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.ktx.jre.cancelAndClear
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.updater.api.FirmwareVersionBuilderApi
import com.flipperdevices.updater.model.FirmwareChannel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

class FullInfoViewModel @Inject constructor(
    private val serviceProvider: FlipperServiceProvider,
    private val flipperRpcInformationApi: FlipperRpcInformationApi,
    private val firmwareVersionBuilderApi: FirmwareVersionBuilderApi
) : DecomposeViewModel(),
    FlipperBleServiceConsumer,
    LogTagProvider {
    override val TAG = "FullInfoViewModel"
    private var jobs = mutableListOf<Job>()
    private val flipperRpcInformationState =
        MutableStateFlow<FlipperInformationStatus<FlipperRpcInformation>>(
            FlipperInformationStatus.NotStarted()
        )

    init {
        serviceProvider.provideServiceApi(this, this)
    }

    fun getFlipperRpcInformation() = flipperRpcInformationState.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            flipperRpcInformationApi.invalidate(
                viewModelScope,
                serviceProvider.getServiceApi(),
                force = true
            )
        }
    }

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) {
        jobs.cancelAndClear()
        jobs += serviceApi.connectionInformationApi.getConnectionStateFlow().onEach {
            when (it) {
                is ConnectionState.Ready -> if (it.supportedState == FlipperSupportedState.READY) {
                    flipperRpcInformationApi.invalidate(
                        viewModelScope + Dispatchers.Default,
                        serviceApi,
                        force = true
                    )
                }
                else -> flipperRpcInformationApi.reset()
            }
        }.launchIn(viewModelScope)

        jobs += flipperRpcInformationApi.getRpcInformationFlow().onEach { rpcInformation ->
            flipperRpcInformationState.emit(rpcInformation)
        }.launchIn(viewModelScope)

        jobs += viewModelScope.launch {
            flipperRpcInformationApi.invalidate(
                viewModelScope + Dispatchers.Default,
                serviceApi
            )
        }
    }

    fun getFirmwareChannel(commit: String?): FirmwareChannel? {
        if (commit == null) return null
        val preparedCommit = commit.split(" ")
        if (preparedCommit.isEmpty()) return null
        val branch = preparedCommit.first()
        return firmwareVersionBuilderApi.getFirmwareChannel(branch)
    }
}
