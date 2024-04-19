package com.flipperdevices.wearable.emulate.impl.helper

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.wearable.emulate.api.HandheldProcessor
import com.flipperdevices.wearable.emulate.common.WearableCommandInputStream
import com.flipperdevices.wearable.emulate.common.WearableCommandOutputStream
import com.flipperdevices.wearable.emulate.common.ipcemulate.Main
import com.flipperdevices.wearable.emulate.common.ipcemulate.mainRequest
import com.flipperdevices.wearable.emulate.common.ipcemulate.requests.pingRequest
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

interface ConnectionHelper {
    fun getState(): StateFlow<ConnectionTesterState>

    fun testConnection()
}

@Singleton
@ContributesMultibinding(AppGraph::class, HandheldProcessor::class)
@ContributesBinding(AppGraph::class, ConnectionHelper::class)
class ConnectionHelperImpl @Inject constructor(
    private val commandInputStream: WearableCommandInputStream<Main.MainResponse>,
    private val commandOutputStream: WearableCommandOutputStream<Main.MainRequest>,
) : ConnectionHelper, HandheldProcessor, LogTagProvider {
    override val TAG: String = "ConnectionTester-${hashCode()}"

    private val state = MutableStateFlow(ConnectionTesterState.NOT_CONNECTED)

    override fun init(scope: CoroutineScope) {
        commandInputStream.getRequestsFlow().onEach {
            if (it.hasPing()) {
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
            mainRequest {
                ping = pingRequest { }
            }
        )
    }
}

enum class ConnectionTesterState {
    NOT_CONNECTED,
    CONNECTED
}
