package com.flipperdevices.info.impl.viewmodel.deviceinfo

import com.flipperdevices.bridge.connection.feature.getinfo.api.FGattInfoFeatureApi
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureStatus
import com.flipperdevices.bridge.connection.feature.provider.api.get
import com.flipperdevices.bridge.connection.feature.rpcinfo.model.FlipperInformationStatus
import com.flipperdevices.bridge.connection.feature.storageinfo.api.FStorageInfoFeatureApi
import com.flipperdevices.bridge.connection.feature.storageinfo.model.FlipperStorageInformation
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.info.impl.model.FlipperBasicInfo
import com.flipperdevices.updater.api.FirmwareVersionBuilderApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class BasicInfoViewModel @Inject constructor(
    private val fFeatureProvider: FFeatureProvider,
    private val firmwareVersionBuilderApi: FirmwareVersionBuilderApi
) : DecomposeViewModel(), LogTagProvider {
    override val TAG = "DeviceInfoViewModel"
    private val flipperBasicInfoState = MutableStateFlow(FlipperBasicInfo())
    fun getDeviceInfo() = flipperBasicInfoState.asStateFlow()

    private fun collectStorageInfo() {
        fFeatureProvider.get<FStorageInfoFeatureApi>()
            .map { it as? FFeatureStatus.Supported<FStorageInfoFeatureApi> }
            .flatMapLatest {
                it?.featureApi
                    ?.getStorageInformationFlow()
                    ?: flowOf(FlipperStorageInformation())
            }.onEach { storageInfo ->
                flipperBasicInfoState.update { basicInfo -> basicInfo.copy(storageInfo = storageInfo) }
            }.launchIn(viewModelScope)
    }

    private fun invalidateStorageInfo() {
        fFeatureProvider.get<FStorageInfoFeatureApi>()
            .filterIsInstance<FFeatureStatus.Supported<FStorageInfoFeatureApi>>()
            .onEach { status -> status.featureApi.invalidate(viewModelScope, true) }
            .launchIn(viewModelScope)
    }

    private fun collectFirmwareInformation() {
        fFeatureProvider.get<FGattInfoFeatureApi>()
            .map { it as? FFeatureStatus.Supported<FGattInfoFeatureApi> }
            .flatMapLatest { it?.featureApi?.getGattInfoFlow() ?: flowOf(null) }
            .map { it?.softwareVersion }
            .onEach { softwareVersion ->
                if (softwareVersion == null) {
                    flipperBasicInfoState.update { basicInfo ->
                        basicInfo.copy(firmwareVersion = FlipperInformationStatus.NotStarted())
                    }
                } else {
                    flipperBasicInfoState.update { basicInfo ->
                        basicInfo.copy(
                            firmwareVersion = FlipperInformationStatus.Ready(
                                data = firmwareVersionBuilderApi.buildFirmwareVersionFromString(
                                    firmwareVersion = softwareVersion
                                )
                            )
                        )
                    }
                }
            }.launchIn(viewModelScope)
    }

    init {
        collectStorageInfo()
        invalidateStorageInfo()
        collectFirmwareInformation()
    }
}
