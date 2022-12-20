package com.flipperdevices.info.impl.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.api.model.FlipperRequestRpcInformationStatus
import com.flipperdevices.bridge.api.model.FlipperRpcInformation
import com.flipperdevices.bridge.api.model.StorageStats
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.ktx.jre.createClearNewFileWithMkDirs
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.share.SharableFile
import com.flipperdevices.core.share.ShareHelper
import com.flipperdevices.core.ui.lifecycle.AndroidLifecycleViewModel
import com.flipperdevices.info.impl.R
import com.flipperdevices.info.impl.model.DeviceInfo
import com.flipperdevices.info.impl.model.DeviceInfoRequestStatus
import com.flipperdevices.updater.api.FirmwareVersionBuilderApi
import com.flipperdevices.updater.api.FlipperVersionProviderApi
import com.flipperdevices.updater.model.FirmwareChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import tangle.viewmodel.VMInject
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FullInfoViewModel @VMInject constructor(
    serviceProvider: FlipperServiceProvider,
    private val flipperVersionProviderApi: FlipperVersionProviderApi,
    private val firmwareVersionBuilderApi: FirmwareVersionBuilderApi,
    application: Application
) : AndroidLifecycleViewModel(application),
    FlipperBleServiceConsumer,
    LogTagProvider {
    override val TAG = "FullInfoViewModel"
    private val deviceInfoState = MutableStateFlow(DeviceInfo())
    private val flipperRpcInformationState = MutableStateFlow(FlipperRpcInformation())
    private val deviceInfoRequestStatus = MutableStateFlow(DeviceInfoRequestStatus())

    init {
        serviceProvider.provideServiceApi(this, this)
    }

    fun getFlipperRpcInformation(): StateFlow<FlipperRpcInformation> = flipperRpcInformationState
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

    fun getFirmwareChannel(commit: String?): FirmwareChannel? {
        if (commit == null) return null
        val preparedCommit = commit.split(" ")
        if (preparedCommit.isEmpty()) return null
        val branch = preparedCommit.first()
        return firmwareVersionBuilderApi.getFirmwareChannel(branch)
    }

    fun shareDeviceInfo() {
        val context = getApplication<Application>().applicationContext

        val file = SharableFile(nameFile = getFileName(), context = context)
        file.createClearNewFileWithMkDirs()

        try {
            addInfoToFile(file)
            ShareHelper.shareFile(
                context = context,
                file = file,
                resId = R.string.device_info_share
            )
        } catch (@Suppress("SwallowedException") exception: Exception) {
            error(exception) { "Exception when upload device info: $exception" }
        }
    }

    private fun getFileName(): String {
        val flipperName = flipperRpcInformationState.value.flipperDeviceInfo.deviceName ?: "unknown"
        val formatDate = LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss")
        )
        return "dump-$flipperName-$formatDate.txt"
    }

    private fun addInfoToFile(file: File) {
        val builder = StringBuilder()
        flipperRpcInformationState.value.allFields.forEach { (key, value) ->
            builder.appendLine("$key: $value")
        }
        flipperRpcInformationState.value.let {
            val intStorage = it.internalStorageStats
            if (intStorage is StorageStats.Loaded) {
                builder.appendLine("int_available: ${intStorage.free}")
                builder.appendLine("int_total: ${intStorage.total}")
            }

            val extStorage = it.externalStorageStats
            if (extStorage is StorageStats.Loaded) {
                builder.appendLine("ext_available: ${extStorage.free}")
                builder.appendLine("ext_total: ${extStorage.total}")
            }
        }
        file.appendText(builder.toString())
    }
}
