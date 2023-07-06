package com.flipperdevices.bridge.service.impl.utils

import com.flipperdevices.bridge.api.manager.delegates.FlipperConnectionInformationApi
import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class WeakConnectionStateProvider @Inject constructor(private val scope: CoroutineScope) {
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
