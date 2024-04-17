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
import com.flipperdevices.wearable.emulate.common.ipcemulate.requests.ConnectStatusOuterClass
import com.flipperdevices.wearable.emulate.common.ipcemulate.requests.subscribeOnConnectStatusRequest
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
    fun getState(): StateFlow<ConnectStatusOuterClass.ConnectStatus>

    fun onSubscribe()
}

@Singleton
@ContributesBinding(AppGraph::class, FlipperStatusHelper::class)
@ContributesMultibinding(AppGraph::class, HandheldProcessor::class)
class FlipperStatusHelperImpl @Inject constructor(
    private val commandInputStream: WearableCommandInputStream<Main.MainResponse>,
    private val commandOutputStream: WearableCommandOutputStream<Main.MainRequest>,
) : FlipperStatusHelper, HandheldProcessor, LogTagProvider {
    override val TAG: String = "FlipperStatusHelper"

    private val state = MutableStateFlow(ConnectStatusOuterClass.ConnectStatus.UNRECOGNIZED)
    override fun getState() = state.asStateFlow()

    override fun init(scope: CoroutineScope) {
        commandInputStream.getRequestsFlow().onEach {
            if (it.hasConnectStatus()) {
                info { "#hasConnectStatus $it" }
                state.emit(it.connectStatus)
            }
        }.launchIn(scope)
    }

    override fun reset(scope: CoroutineScope) {
        scope.launch(FlipperDispatchers.workStealingDispatcher) {
            state.emit(ConnectStatusOuterClass.ConnectStatus.UNRECOGNIZED)
        }
    }

    override fun onSubscribe() {
        commandOutputStream.send(
            mainRequest {
                subscribeOnConnectStatus = subscribeOnConnectStatusRequest { }
            }
        )
    }
}
