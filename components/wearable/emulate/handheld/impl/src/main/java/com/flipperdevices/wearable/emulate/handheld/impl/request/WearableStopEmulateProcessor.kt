package com.flipperdevices.wearable.emulate.handheld.impl.request

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.keyscreen.api.EmulateHelper
import com.flipperdevices.wearable.emulate.common.WearableCommandInputStream
import com.flipperdevices.wearable.emulate.common.WearableCommandOutputStream
import com.flipperdevices.wearable.emulate.common.ipcemulate.Main
import com.flipperdevices.wearable.emulate.common.ipcemulate.mainResponse
import com.flipperdevices.wearable.emulate.common.ipcemulate.requests.Emulate
import com.flipperdevices.wearable.emulate.handheld.impl.di.WearHandheldGraph
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.plus

@ContributesMultibinding(WearHandheldGraph::class, WearableCommandProcessor::class)
class WearableStopEmulateProcessor @Inject constructor(
    private val commandInputStream: WearableCommandInputStream<Main.MainRequest>,
    private val commandOutputStream: WearableCommandOutputStream<Main.MainResponse>,
    private val lifecycleOwner: LifecycleOwner,
    private val serviceProvider: FlipperServiceProvider,
    private val emulateHelper: EmulateHelper
) : WearableCommandProcessor {
    override fun init() {
        commandInputStream.getRequestsFlow().onEach {
            if (it.hasStartEmulate()) {
                stopEmulate(serviceProvider.getServiceApi().requestApi)
            }
        }.launchIn(lifecycleOwner.lifecycleScope + Dispatchers.Default)
    }

    private suspend fun stopEmulate(requestApi: FlipperRequestApi) {
        emulateHelper.stopEmulate(lifecycleOwner.lifecycleScope, requestApi)
        commandOutputStream.send(mainResponse {
            emulateStatus = Emulate.EmulateStatus.STOPPED
        })
    }
}