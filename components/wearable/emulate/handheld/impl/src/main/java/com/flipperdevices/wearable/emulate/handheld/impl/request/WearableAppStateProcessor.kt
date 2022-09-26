package com.flipperdevices.wearable.emulate.handheld.impl.request

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.protobuf.app.Application
import com.flipperdevices.wearable.emulate.common.WearableCommandOutputStream
import com.flipperdevices.wearable.emulate.common.ipcemulate.Main
import com.flipperdevices.wearable.emulate.common.ipcemulate.mainResponse
import com.flipperdevices.wearable.emulate.common.ipcemulate.requests.EmulateStatusOuterClass
import com.flipperdevices.wearable.emulate.handheld.impl.di.WearHandheldGraph
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.plus

@ContributesMultibinding(WearHandheldGraph::class, WearableCommandProcessor::class)
class WearableAppStateProcessor @Inject constructor(
    private val serviceProvider: FlipperServiceProvider,
    private val commandOutputStream: WearableCommandOutputStream<Main.MainResponse>,
    private val lifecycleOwner: LifecycleOwner
) : WearableCommandProcessor, FlipperBleServiceConsumer {


    override fun init() {
        serviceProvider.provideServiceApi(this@WearableAppStateProcessor, lifecycleOwner)
    }

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) {
        serviceApi.requestApi.notificationFlow().onEach { unknownMessage ->
            if (unknownMessage.hasAppStateResponse()) {
                if (unknownMessage.appStateResponse.state == Application.AppState.APP_CLOSED) {
                    commandOutputStream.send(mainResponse {
                        emulateStatus = EmulateStatusOuterClass.EmulateStatus.STOP_EMULATE
                    })
                }
            }
        }.launchIn(lifecycleOwner.lifecycleScope + Dispatchers.Default)
    }

}