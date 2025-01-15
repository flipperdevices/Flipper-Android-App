package com.flipperdevices.wearable.emulate.handheld.impl.request

import com.flipperdevices.bridge.connection.feature.emulate.api.FEmulateFeatureApi
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.getSync
import com.flipperdevices.core.di.SingleIn
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
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

@SingleIn(WearHandheldGraph::class)
@ContributesMultibinding(WearHandheldGraph::class, WearableCommandProcessor::class)
class WearableStopEmulateProcessor @Inject constructor(
    private val commandInputStream: WearableCommandInputStream<Main.MainRequest>,
    private val commandOutputStream: WearableCommandOutputStream<Main.MainResponse>,
    private val scope: CoroutineScope,
    private val fFeatureProvider: FFeatureProvider
) : WearableCommandProcessor, LogTagProvider {
    override val TAG: String = "WearableStopEmulateProcessor-${hashCode()}"

    override fun init() {
        commandInputStream.getRequestsFlow().onEach {
            if (it.hasStopEmulate()) {
                info { "StopEmulate: ${it.stopEmulate}" }
                stopEmulate()
            }
        }.launchIn(scope)
    }

    private suspend fun stopEmulate() {
        val fEmulateApi = fFeatureProvider.getSync<FEmulateFeatureApi>() ?: run {
            error { "#onStartEmulateInternal could not get emulate api" }
            return
        }
        val emulateHelper = fEmulateApi.getEmulateHelper()
        try {
            emulateHelper.stopEmulate(scope)
            commandOutputStream.send(
                mainResponse {
                    emulateStatus = Emulate.EmulateStatus.STOPPED
                }
            )
        } catch (throwable: Throwable) {
            error(throwable) { "Failed stop emulate" }
            commandOutputStream.send(
                mainResponse {
                    emulateStatus = Emulate.EmulateStatus.FAILED
                }
            )
        }
    }
}
