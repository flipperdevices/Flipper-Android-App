package com.flipperdevices.info.impl.viewmodel

import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.ui.LifecycleViewModel
import com.flipperdevices.info.impl.di.InfoComponent
import com.flipperdevices.info.impl.model.DeviceInfo
import com.flipperdevices.info.impl.model.FirmwareVersion
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

private const val DEVICE_VERSION_PART_COUNT = 4
private const val DEVICE_VERSION_COMMIT_INDEX = 1
private const val DEVICE_VERSION_TYPE_INDEX = 2
private const val DEVICE_VERSION_TYPE_DEV = "dev"
private const val DEVICE_VERSION_TYPE_RC = "rc"
private const val DEVICE_VERSION_DATE_INDEX = 4

class DeviceInfoViewModel :
    LifecycleViewModel(),
    FlipperBleServiceConsumer,
    LogTagProvider {
    override val TAG = "DeviceInfoViewModel"
    private val deviceInfoState = MutableStateFlow(DeviceInfo())

    @Inject
    lateinit var serviceProvider: FlipperServiceProvider

    init {
        ComponentHolder.component<InfoComponent>().inject(this)
        serviceProvider.provideServiceApi(this, this)
    }

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) {
        serviceApi.flipperInformationApi.getInformationFlow().onEach {
            val softwareVersion = it.softwareVersion
            if (softwareVersion != null) {
                buildFirmwareVersionFromString(softwareVersion).let { firmwareVersion ->
                    deviceInfoState.update { it.copy(firmwareVersion = firmwareVersion) }
                }
            }
        }.launchIn(viewModelScope)
        serviceApi.flipperRpcInformationApi.getRpcInformationFlow().onEach {
            info { "Receive flipper information $it" }
        }.launchIn(viewModelScope)
    }

    private fun buildFirmwareVersionFromString(
        firmwareVersion: String
    ): FirmwareVersion {
        val unparsedArray = firmwareVersion.split(" ").filterNot { it.isBlank() }
        if (unparsedArray.size < DEVICE_VERSION_PART_COUNT) {
            return FirmwareVersion.Unknown
        }
        val hash = unparsedArray[DEVICE_VERSION_COMMIT_INDEX]
        val typeVersion = unparsedArray[DEVICE_VERSION_TYPE_INDEX]
        val date = unparsedArray[DEVICE_VERSION_DATE_INDEX]

        if (typeVersion.trim() == DEVICE_VERSION_TYPE_DEV) {
            return FirmwareVersion.Dev(hash, date)
        }

        if (typeVersion.contains(DEVICE_VERSION_TYPE_RC)) {
            return FirmwareVersion.ReleaseCandidate(
                typeVersion.replace(
                    "-$DEVICE_VERSION_TYPE_RC",
                    ""
                ),
                date
            )
        }

        return FirmwareVersion.Release(typeVersion, date)
    }
}
