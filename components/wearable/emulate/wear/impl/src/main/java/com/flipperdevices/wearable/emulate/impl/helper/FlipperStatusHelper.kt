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

interface FlipperStatusHelper {
    fun getState(): StateFlow<ConnectStatus>

    fun onSubscribe()
}

@SingleIn(AppGraph::class)
@ContributesBinding(AppGraph::class, binding<FlipperStatusHelper>())
@ContributesIntoSet(AppGraph::class, binding<HandheldProcessor>())
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
