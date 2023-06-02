package com.flipperdevices.wearable.emulate.handheld.impl.request

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.keyemulate.api.EmulateHelper
import com.flipperdevices.wearable.emulate.common.WearableCommandInputStream
import com.flipperdevices.wearable.emulate.common.WearableCommandOutputStream
import com.flipperdevices.wearable.emulate.common.ipcemulate.Main
import com.flipperdevices.wearable.emulate.common.ipcemulate.mainResponse
import com.flipperdevices.wearable.emulate.common.ipcemulate.requests.Emulate
import com.flipperdevices.wearable.emulate.handheld.impl.di.WearHandheldGraph
import com.squareup.anvil.annotations.ContributesMultibinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@ContributesMultibinding(WearHandheldGraph::class, WearableCommandProcessor::class)
class WearableStopEmulateProcessor @Inject constructor(
    private val commandInputStream: WearableCommandInputStream<Main.MainRequest>,
    private val commandOutputStream: WearableCommandOutputStream<Main.MainResponse>,
    private val scope: CoroutineScope,
    private val serviceProvider: FlipperServiceProvider,
    private val emulateHelper: EmulateHelper
) : WearableCommandProcessor {
    override fun init() {
        commandInputStream.getRequestsFlow().onEach {
            if (it.hasStopEmulate()) {
                stopEmulate(serviceProvider.getServiceApi().requestApi)
            }
        }.launchIn(scope)
    }

    private suspend fun stopEmulate(requestApi: FlipperRequestApi) {
        emulateHelper.stopEmulate(scope, requestApi)
        commandOutputStream.send(
            mainResponse {
                emulateStatus = Emulate.EmulateStatus.STOPPED
            }
        )
    }
}
