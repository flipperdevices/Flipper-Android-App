package com.flipperdevices.wearable.emulate.impl.helper

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.wearable.emulate.api.HandheldProcessor
import com.flipperdevices.wearable.emulate.common.WearableCommandInputStream
import com.flipperdevices.wearable.emulate.common.ipcemulate.Main
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

interface ConnectionTester {
    fun getState(): StateFlow<ConnectionTesterState>
}

@Singleton
@ContributesMultibinding(AppGraph::class, HandheldProcessor::class)
@ContributesBinding(AppGraph::class, ConnectionTester::class)
class ConnectionTesterImpl @Inject constructor(
    private val commandInputStream: WearableCommandInputStream<Main.MainResponse>,
) : ConnectionTester, HandheldProcessor, LogTagProvider {
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

    override suspend fun reset() {
        info { "reset" }
        state.emit(ConnectionTesterState.NOT_CONNECTED)
    }

    override fun getState(): StateFlow<ConnectionTesterState> = state.asStateFlow()
}

enum class ConnectionTesterState {
    NOT_CONNECTED,
    CONNECTED
}
