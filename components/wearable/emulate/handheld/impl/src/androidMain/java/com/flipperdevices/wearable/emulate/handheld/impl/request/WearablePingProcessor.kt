package com.flipperdevices.wearable.emulate.handheld.impl.request

import com.flipperdevices.core.di.SingleIn
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.wearable.emulate.common.WearableCommandInputStream
import com.flipperdevices.wearable.emulate.common.WearableCommandOutputStream
import com.flipperdevices.wearable.emulate.common.ipcemulate.Main
import com.flipperdevices.wearable.emulate.common.ipcemulate.mainResponse
import com.flipperdevices.wearable.emulate.common.ipcemulate.requests.pingResponse
import com.flipperdevices.wearable.emulate.handheld.impl.di.WearHandheldGraph
import com.squareup.anvil.annotations.ContributesMultibinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@SingleIn(WearHandheldGraph::class)
@ContributesMultibinding(WearHandheldGraph::class, WearableCommandProcessor::class)
class WearablePingProcessor @Inject constructor(
    private val commandInputStream: WearableCommandInputStream<Main.MainRequest>,
    private val commandOutputStream: WearableCommandOutputStream<Main.MainResponse>,
    private val scope: CoroutineScope,
) : WearableCommandProcessor, LogTagProvider {
    override val TAG: String = "WearablePingProcessor-${hashCode()}"

    override fun init() {
        commandOutputStream.send(
            mainResponse {
                ping = pingResponse { }
            }
        )
        commandInputStream.getRequestsFlow().onEach {
            if (it.hasPing()) {
                info { "Ping: ${it.ping}" }
                commandOutputStream.send(mainResponse { ping = pingResponse { } })
            }
        }.launchIn(scope)
    }
}
