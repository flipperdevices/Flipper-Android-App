package com.flipperdevices.info.impl.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.api.model.FlipperGATTInformation
import com.flipperdevices.bridge.api.model.FlipperRequestRpcInformationStatus
import com.flipperdevices.bridge.api.model.FlipperRpcInformation
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ktx.jre.appendNewLine
import com.flipperdevices.core.ktx.jre.createClearNewFileWithMkDirs
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.preference.FlipperStorageProvider
import com.flipperdevices.core.share.ShareHelper
import com.flipperdevices.core.ui.lifecycle.AndroidLifecycleViewModel
import com.flipperdevices.info.impl.R
import com.flipperdevices.info.impl.di.InfoComponent
import com.flipperdevices.info.impl.model.DeviceInfo
import com.flipperdevices.info.impl.model.DeviceInfoRequestStatus
import com.flipperdevices.info.impl.model.toString
import com.flipperdevices.updater.api.FirmwareVersionBuilderApi
import com.flipperdevices.updater.api.FlipperVersionProviderApi
import com.flipperdevices.updater.model.FirmwareChannel
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import tangle.viewmodel.VMInject

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
    private val flipperGATTInformation = MutableStateFlow(FlipperGATTInformation())

    init {
        ComponentHolder.component<InfoComponent>().inject(this)
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

        serviceApi.flipperInformationApi.getInformationFlow().onEach {
            flipperGATTInformation.emit(it)
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

        val nameFile = getFileName()
        val file = File(FlipperStorageProvider.getSharedKeyFolder(context), nameFile)
        file.createClearNewFileWithMkDirs()

        try {
            addInfoToFile(file, context)
            ShareHelper.shareFile(
                context = context,
                file = file,
                name = nameFile,
                resId = R.string.device_info_share
            )
        } catch (exception: Exception) {
            error { "Exception when upload device info" }
        }
    }

    private fun getFileName(): String {
        val flipperName = flipperRpcInformationState.value.flipperDeviceInfo.deviceName ?: "unknown"
        val formatDate = LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss")
        )
        return "dump-$flipperName-$formatDate.txt"
    }

    private fun addInfoToFile(file: File, context: Context) {
        flipperRpcInformationState.value.allFields.forEach { (t, u) ->
            file.appendNewLine("$t:$u")
        }
        flipperRpcInformationState.value.let {
            val int = it.internalStorageStats?.toString(context)
                ?: context.getString(R.string.info_device_info_flash_not_found)
            file.appendNewLine("int_storage:$int")
            val ext = it.externalStorageStats?.toString(context)
                ?: context.getString(R.string.info_device_info_flash_not_found)
            file.appendNewLine("ext_storage:$ext")
        }
        flipperGATTInformation.value.let {
            file.appendNewLine("is_charging:${it.isCharging}")
            file.appendNewLine("battery_level:${it.batteryLevel}")
        }
    }
}
