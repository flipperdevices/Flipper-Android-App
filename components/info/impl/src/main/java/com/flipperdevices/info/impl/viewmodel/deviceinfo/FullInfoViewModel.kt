package com.flipperdevices.info.impl.viewmodel.deviceinfo

import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureStatus
import com.flipperdevices.bridge.connection.feature.provider.api.get
import com.flipperdevices.bridge.connection.feature.provider.api.getSync
import com.flipperdevices.bridge.connection.feature.rpcinfo.api.FRpcInfoFeatureApi
import com.flipperdevices.bridge.connection.feature.rpcinfo.model.FlipperInformationStatus
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.updater.api.FirmwareVersionBuilderApi
import com.flipperdevices.updater.model.FirmwareChannel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class FullInfoViewModel @Inject constructor(
    private val firmwareVersionBuilderApi: FirmwareVersionBuilderApi,
    private val fFeatureProvider: FFeatureProvider
) : DecomposeViewModel(),
    LogTagProvider {
    override val TAG = "FullInfoViewModel"

    private val flipperRpcInformation = fFeatureProvider.get<FRpcInfoFeatureApi>()
        .map { status -> status as? FFeatureStatus.Supported<FRpcInfoFeatureApi> }
        .flatMapLatest { status -> status?.featureApi?.getRpcInformationFlow() ?: flowOf(null) }
        .map { informationStatus -> informationStatus ?: FlipperInformationStatus.NotStarted() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, FlipperInformationStatus.NotStarted())

    fun getFlipperRpcInformation() = flipperRpcInformation

    private fun invalidateRpcInfo() {
        fFeatureProvider.get<FRpcInfoFeatureApi>()
            .map { status -> status as? FFeatureStatus.Supported<FRpcInfoFeatureApi> }
            .onEach { status -> status?.featureApi?.invalidate(viewModelScope, force = true) }
            .launchIn(viewModelScope)
    }

    fun getFirmwareChannel(commit: String?): FirmwareChannel? {
        if (commit == null) return null
        val preparedCommit = commit.split(" ")
        if (preparedCommit.isEmpty()) return null
        val branch = preparedCommit.first()
        return firmwareVersionBuilderApi.getFirmwareChannel(branch)
    }

    fun refresh() {
        viewModelScope.launch {
            fFeatureProvider.getSync<FRpcInfoFeatureApi>()
                ?.invalidate(viewModelScope, force = true)
        }
    }

    init {
        invalidateRpcInfo()
    }
}
