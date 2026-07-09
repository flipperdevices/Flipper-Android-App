package com.flipperdevices.wearable.emulate.handheld.impl.request

import com.flipperdevices.bridge.connection.feature.emulate.api.FEmulateFeatureApi
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.getSync
import dev.zacsweers.metro.SingleIn
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.wearable.emulate.common.WearableCommandInputStream
import com.flipperdevices.wearable.emulate.common.WearableCommandOutputStream
import com.flipperdevices.wearable.emulate.common.ipcemulate.MainRequest
import com.flipperdevices.wearable.emulate.common.ipcemulate.MainResponse
import com.flipperdevices.wearable.emulate.common.ipcemulate.requests.EmulateStatus
import com.flipperdevices.wearable.emulate.handheld.impl.di.WearHandheldGraph
import dev.zacsweers.metro.ContributesIntoSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding

@SingleIn(WearHandheldGraph::class)
@ContributesIntoSet(WearHandheldGraph::class, binding<WearableCommandProcessor>())
class WearableStopEmulateProcessor @Inject constructor(
    private val commandInputStream: WearableCommandInputStream<MainRequest>,
    private val commandOutputStream: WearableCommandOutputStream<MainResponse>,
    private val scope: CoroutineScope,
    private val fFeatureProvider: FFeatureProvider
) : WearableCommandProcessor, LogTagProvider {
    override val TAG: String = "WearableStopEmulateProcessor-${hashCode()}"

    override fun init() {
        commandInputStream.getRequestsFlow().onEach {
            if (it.stop_emulate != null) {
                info { "StopEmulate: ${it.stop_emulate}" }
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
                MainResponse(emulate_status = EmulateStatus.STOPPED)
            )
        } catch (throwable: Throwable) {
            error(throwable) { "Failed stop emulate" }
            commandOutputStream.send(
                MainResponse(emulate_status = EmulateStatus.FAILED)
            )
        }
    }
}
