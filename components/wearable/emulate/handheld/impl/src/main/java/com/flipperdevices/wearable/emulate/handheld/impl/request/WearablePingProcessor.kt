package com.flipperdevices.wearable.emulate.handheld.impl.request

import dev.zacsweers.metro.SingleIn
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.wearable.emulate.common.WearableCommandInputStream
import com.flipperdevices.wearable.emulate.common.WearableCommandOutputStream
import com.flipperdevices.wearable.emulate.common.ipcemulate.MainRequest
import com.flipperdevices.wearable.emulate.common.ipcemulate.MainResponse
import com.flipperdevices.wearable.emulate.common.ipcemulate.requests.PingResponse
import com.flipperdevices.wearable.emulate.handheld.impl.di.WearHandheldGraph
import dev.zacsweers.metro.ContributesIntoSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding

@SingleIn(WearHandheldGraph::class)
@ContributesIntoSet(WearHandheldGraph::class, binding<WearableCommandProcessor>())
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
