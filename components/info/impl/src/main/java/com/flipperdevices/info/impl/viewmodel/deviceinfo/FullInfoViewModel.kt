package com.flipperdevices.info.impl.viewmodel.deviceinfo

import android.app.Application
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.ui.lifecycle.AndroidLifecycleViewModel
import com.flipperdevices.info.api.model.FlipperInformationStatus
import com.flipperdevices.info.api.model.FlipperRpcInformation
import com.flipperdevices.updater.api.FirmwareVersionBuilderApi
import com.flipperdevices.updater.model.FirmwareChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import tangle.viewmodel.VMInject

class FullInfoViewModel @VMInject constructor(
    serviceProvider: FlipperServiceProvider,
    private val firmwareVersionBuilderApi: FirmwareVersionBuilderApi,
    application: Application
) : AndroidLifecycleViewModel(application),
    FlipperBleServiceConsumer,
    LogTagProvider {
    override val TAG = "FullInfoViewModel"
    private val flipperRpcInformationState =
        MutableStateFlow<FlipperInformationStatus<FlipperRpcInformation>>(
            FlipperInformationStatus.NotStarted()
        )

    init {
        serviceProvider.provideServiceApi(this, this)
    }

    fun getFlipperRpcInformation() = flipperRpcInformationState.asStateFlow()

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) {
    }

    fun getFirmwareChannel(commit: String?): FirmwareChannel? {
        if (commit == null) return null
        val preparedCommit = commit.split(" ")
        if (preparedCommit.isEmpty()) return null
        val branch = preparedCommit.first()
        return firmwareVersionBuilderApi.getFirmwareChannel(branch)
    }
}
