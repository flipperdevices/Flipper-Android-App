package com.flipperdevices.bridge.service.impl.utils

import com.flipperdevices.bridge.api.manager.delegates.FlipperConnectionInformationApi
import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.concurrent.atomic.AtomicBoolean

class WeakConnectionStateProvider(private val scope: CoroutineScope) {
    private val connectionFlow = MutableSharedFlow<ConnectionState>()
    private val inited = AtomicBoolean(false)

    fun initialize(connectionInformationApi: FlipperConnectionInformationApi) {
        if (!inited.compareAndSet(false, true)) {
            return
        }
        connectionInformationApi.getConnectionStateFlow().onEach {
            connectionFlow.emit(it)
        }.launchIn(scope)
    }

    fun getConnectionFlow() = connectionFlow
}
