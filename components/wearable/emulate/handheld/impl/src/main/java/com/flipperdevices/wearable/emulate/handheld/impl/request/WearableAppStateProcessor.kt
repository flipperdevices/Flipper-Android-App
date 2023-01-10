package com.flipperdevices.wearable.emulate.handheld.impl.request

import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.protobuf.app.Application
import com.flipperdevices.wearable.emulate.common.WearableCommandOutputStream
import com.flipperdevices.wearable.emulate.common.ipcemulate.Main
import com.flipperdevices.wearable.emulate.common.ipcemulate.mainResponse
import com.flipperdevices.wearable.emulate.common.ipcemulate.requests.Emulate
import com.flipperdevices.wearable.emulate.handheld.impl.di.WearHandheldGraph
import com.squareup.anvil.annotations.ContributesMultibinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@ContributesMultibinding(WearHandheldGraph::class, WearableCommandProcessor::class)
class WearableAppStateProcessor @Inject constructor(
    private val serviceProvider: FlipperServiceProvider,
    private val commandOutputStream: WearableCommandOutputStream<Main.MainResponse>,
    private val scope: CoroutineScope
) : WearableCommandProcessor {
    override fun init() {
        scope.launch {
            serviceProvider.getServiceApi().requestApi.notificationFlow().collect {
                if (it.hasAppStateResponse()) {
                    if (it.appStateResponse.state == Application.AppState.APP_CLOSED) {
                        commandOutputStream.send(
                            mainResponse {
                                emulateStatus = Emulate.EmulateStatus.STOPPED
                            }
                        )
                    }
                }
            }
        }
    }
}
