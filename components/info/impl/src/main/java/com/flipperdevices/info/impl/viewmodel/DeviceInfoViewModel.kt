package com.flipperdevices.info.impl.viewmodel

import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.api.model.FlipperRequestRpcInformationStatus
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.ui.LifecycleViewModel
import com.flipperdevices.info.impl.di.InfoComponent
import com.flipperdevices.info.impl.fragment.FullDeviceInfoFragment
import com.flipperdevices.info.impl.model.DeviceInfo
import com.flipperdevices.info.impl.model.DeviceInfoRequestStatus
import com.flipperdevices.info.impl.model.StorageInfo
import com.flipperdevices.info.impl.model.VerboseDeviceInfo
import com.flipperdevices.info.impl.utils.FirmwareVersionBuildHelper
import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.androidx.FragmentScreen
import java.lang.Math.max
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class DeviceInfoViewModel :
    LifecycleViewModel(),
    FlipperBleServiceConsumer,
    LogTagProvider {
    override val TAG = "DeviceInfoViewModel"
    private val deviceInfoState = MutableStateFlow(DeviceInfo())
    private val verboseDeviceInfoState = MutableStateFlow(VerboseDeviceInfo())
    private val deviceInfoRequestStatus = MutableStateFlow(DeviceInfoRequestStatus())

    @Inject
    lateinit var serviceProvider: FlipperServiceProvider

    init {
        ComponentHolder.component<InfoComponent>().inject(this)
        serviceProvider.provideServiceApi(this, this)
    }

    fun getDeviceInfo(): StateFlow<DeviceInfo> = deviceInfoState
    fun getVerboseDeviceInfoState(): StateFlow<VerboseDeviceInfo> = verboseDeviceInfoState
    fun getDeviceInfoRequestStatus(): StateFlow<DeviceInfoRequestStatus> = deviceInfoRequestStatus

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) {
        serviceApi.flipperInformationApi.getInformationFlow().onEach { flipperGATTInformation ->
            val softwareVersion = flipperGATTInformation.softwareVersion
            val softwareVersionParsed = if (softwareVersion != null) FirmwareVersionBuildHelper
                .buildFirmwareVersionFromString(softwareVersion) else null

            deviceInfoState.update { it.copy(firmwareVersion = softwareVersionParsed) }
        }.launchIn(viewModelScope)

        serviceApi.flipperRpcInformationApi.getRpcInformationFlow().onEach { rpcInformation ->
            deviceInfoState.update {
                it.copy(
                    flashInt = getStorageInfo(
                        rpcInformation.internalStorageFree,
                        rpcInformation.internalStorageTotal
                    ),
                    flashSd = getStorageInfo(
                        rpcInformation.externalStorageFree,
                        rpcInformation.externalStorageTotal
                    )
                )
            }
            verboseDeviceInfoState.update {
                it.copy(rpcInformationMap = rpcInformation.otherFields)
            }
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

    fun onOpenFullDeviceInfo(router: Router) {
        router.navigateTo(FragmentScreen { FullDeviceInfoFragment() })
    }

    fun getStorageInfo(free: Long?, total: Long?): StorageInfo? {
        if (free == null || total == null) {
            return null
        }
        return StorageInfo(max(0, total - free), total)
    }
}
