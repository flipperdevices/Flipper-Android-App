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
import com.flipperdevices.wearable.emulate.common.ipcemulate.requests.ConnectStatus
import com.flipperdevices.wearable.emulate.common.ipcemulate.requests.SubscribeOnConnectStatusRequest
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

interface FlipperStatusHelper {
    fun getState(): StateFlow<ConnectStatus>

    fun onSubscribe()
}

@Singleton
@ContributesBinding(AppGraph::class, FlipperStatusHelper::class)
@ContributesMultibinding(AppGraph::class, HandheldProcessor::class)
class FlipperStatusHelperImpl @Inject constructor(
    private val commandInputStream: WearableCommandInputStream<MainResponse>,
    private val commandOutputStream: WearableCommandOutputStream<MainRequest>,
) : FlipperStatusHelper, HandheldProcessor, LogTagProvider {
    override val TAG: String = "FlipperStatusHelper"

    private val state = MutableStateFlow<ConnectStatus>(ConnectStatus.fromValue(-1))
    override fun getState() = state.asStateFlow()

    override fun init(scope: CoroutineScope) {
        commandInputStream.getRequestsFlow().onEach {
            val connectStatus = it.connect_status
            if (connectStatus != null) {
                info { "#hasConnectStatus $it" }
                state.emit(connectStatus)
            }
        }.launchIn(scope)
    }

    override fun reset(scope: CoroutineScope) {
        scope.launch(FlipperDispatchers.workStealingDispatcher) {
            state.emit(ConnectStatus.fromValue(-1))
        }
    }

    override fun onSubscribe() {
        commandOutputStream.send(
            MainRequest(subscribe_on_connect_status = SubscribeOnConnectStatusRequest())
        )
    }
}
