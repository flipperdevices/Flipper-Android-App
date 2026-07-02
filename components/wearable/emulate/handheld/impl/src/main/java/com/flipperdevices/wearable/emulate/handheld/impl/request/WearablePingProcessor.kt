package com.flipperdevices.wearable.emulate.handheld.impl.request

import com.flipperdevices.core.di.SingleIn
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.wearable.emulate.common.WearableCommandInputStream
import com.flipperdevices.wearable.emulate.common.WearableCommandOutputStream
import com.flipperdevices.wearable.emulate.common.ipcemulate.MainRequest
import com.flipperdevices.wearable.emulate.common.ipcemulate.MainResponse
import com.flipperdevices.wearable.emulate.common.ipcemulate.requests.PingResponse
import com.flipperdevices.wearable.emulate.handheld.impl.di.WearHandheldGraph
import com.squareup.anvil.annotations.ContributesMultibinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@SingleIn(WearHandheldGraph::class)
@ContributesMultibinding(WearHandheldGraph::class, WearableCommandProcessor::class)
class WearablePingProcessor @Inject constructor(
    private val commandInputStream: WearableCommandInputStream<MainRequest>,
    private val commandOutputStream: WearableCommandOutputStream<MainResponse>,
    private val scope: CoroutineScope,
) : WearableCommandProcessor, LogTagProvider {
    override val TAG: String = "WearablePingProcessor-${hashCode()}"

    override fun init() {
        commandOutputStream.send(
            MainResponse(ping = PingResponse())
        )
        commandInputStream.getRequestsFlow().onEach {
            if (it.ping != null) {
                info { "Ping: ${it.ping}" }
                commandOutputStream.send(MainResponse(ping = PingResponse()))
            }
        }.launchIn(scope)
    }
}
