package com.flipperdevices.wearable.emulate.impl.helper

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.wearable.emulate.api.HandheldProcessor
import com.flipperdevices.wearable.emulate.common.WearableCommandInputStream
import com.flipperdevices.wearable.emulate.common.WearableCommandOutputStream
import com.flipperdevices.wearable.emulate.common.ipcemulate.MainRequest
import com.flipperdevices.wearable.emulate.common.ipcemulate.MainResponse
import com.flipperdevices.wearable.emulate.common.ipcemulate.requests.PingRequest
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.ContributesIntoSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding

interface ConnectionHelper {
    fun getState(): StateFlow<ConnectionTesterState>

    fun testConnection()
}

@SingleIn(AppGraph::class)
@ContributesIntoSet(AppGraph::class, binding<HandheldProcessor>())
@ContributesBinding(AppGraph::class, binding<ConnectionHelper>())
class ConnectionHelperImpl @Inject constructor(
    private val commandInputStream: WearableCommandInputStream<MainResponse>,
    private val commandOutputStream: WearableCommandOutputStream<MainRequest>,
) : ConnectionHelper, HandheldProcessor, LogTagProvider {
    override val TAG: String = "ConnectionTester-${hashCode()}"

    private val state = MutableStateFlow(ConnectionTesterState.NOT_CONNECTED)

    override fun init(scope: CoroutineScope) {
        commandInputStream.getRequestsFlow().onEach {
            if (it.ping != null) {
                info { "Ping received" }
                state.emit(ConnectionTesterState.CONNECTED)
            }
        }.launchIn(scope)
    }

    override fun reset(scope: CoroutineScope) {
        info { "reset" }
        scope.launch(FlipperDispatchers.workStealingDispatcher) {
            state.emit(ConnectionTesterState.NOT_CONNECTED)
        }
    }

    override fun getState(): StateFlow<ConnectionTesterState> = state.asStateFlow()
    override fun testConnection() {
        commandOutputStream.send(
            MainRequest(ping = PingRequest())
        )
    }
}

enum class ConnectionTesterState {
    NOT_CONNECTED,
    CONNECTED
}
