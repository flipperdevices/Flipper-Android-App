package com.flipperdevices.wearable.emulate.impl.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.wearable.emulate.common.WearableCommandInputStream
import com.flipperdevices.wearable.emulate.common.WearableCommandOutputStream
import com.flipperdevices.wearable.emulate.common.ipcemulate.Main
import com.flipperdevices.wearable.emulate.common.ipcemulate.mainRequest
import com.flipperdevices.wearable.emulate.common.ipcemulate.requests.pingRequest
import com.flipperdevices.wearable.emulate.impl.di.WearGraph
import com.flipperdevices.wearable.emulate.impl.model.ConnectionTesterState
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.plus

interface ConnectionTester {
    fun getState(): StateFlow<ConnectionTesterState>
    suspend fun resetState()
    fun testConnection()
}

@ContributesBinding(WearGraph::class, ConnectionTester::class)
class ConnectionTesterImpl @Inject constructor(
    commandInputStream: WearableCommandInputStream<Main.MainResponse>,
    lifecycleOwner: LifecycleOwner,
    private val commandOutputStream: WearableCommandOutputStream<Main.MainRequest>
) : ConnectionTester, LogTagProvider {
    override val TAG = "ConnectionTester"

    private val connectionTesterStateFlow = MutableStateFlow(ConnectionTesterState.NOT_CONNECTED)

    init {
        commandInputStream.getRequestsFlow().onEach {
            if (it.hasPing()) {
                info { "receive ping" }
                connectionTesterStateFlow.emit(ConnectionTesterState.CONNECTED)
            }
        }.launchIn(lifecycleOwner.lifecycleScope + Dispatchers.Default)
    }

    override fun getState() = connectionTesterStateFlow

    override suspend fun resetState() {
        info { "#resetState" }
        connectionTesterStateFlow.emit(ConnectionTesterState.NOT_CONNECTED)
    }

    override fun testConnection() {
        info { "#testConnection" }
        commandOutputStream.send(mainRequest { ping = pingRequest { } })
    }
}