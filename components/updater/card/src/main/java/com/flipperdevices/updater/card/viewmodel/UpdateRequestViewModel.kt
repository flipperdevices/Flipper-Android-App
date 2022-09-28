package com.flipperdevices.updater.card.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.ktx.jre.getClearName
import com.flipperdevices.core.ui.lifecycle.LifecycleViewModel
import com.flipperdevices.deeplink.api.DeepLinkParser
import com.flipperdevices.deeplink.model.DeeplinkContent
import com.flipperdevices.updater.api.UpdaterUIApi
import com.flipperdevices.updater.card.model.BatteryState
import com.flipperdevices.updater.model.FirmwareChannel
import com.flipperdevices.updater.model.FirmwareVersion
import com.flipperdevices.updater.model.InternalStorageFirmware
import com.flipperdevices.updater.model.UpdateCardState
import com.flipperdevices.updater.model.UpdateRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import tangle.viewmodel.VMInject

class UpdateRequestViewModel @VMInject constructor(
    serviceProvider: FlipperServiceProvider,
    private val updaterUIApi: UpdaterUIApi,
    private val deeplinkParser: DeepLinkParser
) : LifecycleViewModel(), FlipperBleServiceConsumer {

    private val batteryStateFlow = MutableStateFlow<BatteryState>(BatteryState.Unknown)

    init {
        serviceProvider.provideServiceApi(consumer = this, lifecycleOwner = this)
    }

    fun getBatteryState(): StateFlow<BatteryState> = batteryStateFlow

    fun openUpdate(updateAvailable: UpdateCardState.UpdateAvailable) {
        updaterUIApi.openUpdateScreen(
            silent = false,
            updateRequest = updateAvailable.update
        )
    }

    fun openUpdate(uri: Uri, context: Context, currentVersion: FirmwareVersion) {
        viewModelScope.launch {
            val deeplink = deeplinkParser.fromUri(context, uri)
            val content = deeplink?.content
            if (content is DeeplinkContent.InternalStorageFile) {
                updaterUIApi.openUpdateScreen(
                    silent = false,
                    updateRequest = UpdateRequest(
                        updateFrom = currentVersion,
                        updateTo = FirmwareVersion(
                            channel = FirmwareChannel.CUSTOM,
                            version = content.file.getClearName()
                        ),
                        changelog = null,
                        content = InternalStorageFirmware(content.file)
                    )
                )
            }
        }
    }

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) {
        serviceApi.flipperInformationApi.getInformationFlow().onEach {
            val batteryLevel = it.batteryLevel
            if (batteryLevel != null) {
                batteryStateFlow.emit(BatteryState.Ready(it.isCharging, batteryLevel))
            } else batteryStateFlow.emit(BatteryState.Unknown)
        }.launchIn(viewModelScope)
    }
}
