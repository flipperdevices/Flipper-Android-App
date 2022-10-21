package com.flipperdevices.wearable.emulate.impl.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.flipperdevices.core.di.SingleIn
import com.flipperdevices.core.log.info
import com.flipperdevices.wearable.emulate.common.WearableCommandInputStream
import com.flipperdevices.wearable.emulate.common.WearableCommandOutputStream
import com.flipperdevices.wearable.emulate.common.ipcemulate.Main
import com.flipperdevices.wearable.emulate.common.ipcemulate.mainRequest
import com.flipperdevices.wearable.emulate.common.ipcemulate.requests.ConnectStatusOuterClass
import com.flipperdevices.wearable.emulate.common.ipcemulate.requests.subscribeOnConnectStatusRequest
import com.flipperdevices.wearable.emulate.impl.di.WearGraph
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.plus

interface FlipperStatusListener {
    fun requestStatus()
    fun getState(): StateFlow<ConnectStatusOuterClass.ConnectStatus>
}

@SingleIn(WearGraph::class)
@ContributesBinding(WearGraph::class, FlipperStatusListener::class)
class FlipperStatusListenerImpl @Inject constructor(
    commandInputStream: WearableCommandInputStream<Main.MainResponse>,
    lifecycleOwner: LifecycleOwner,
    private val commandOutputStream: WearableCommandOutputStream<Main.MainRequest>
) : FlipperStatusListener {
    private val connectionStatusStateFlow = MutableStateFlow(
        ConnectStatusOuterClass.ConnectStatus.UNSUPPORTED
    )

    init {
        commandInputStream.getRequestsFlow().onEach {
            if (it.hasConnectStatus()) {
                info { "receive ping" }
                connectionStatusStateFlow.emit(it.connectStatus)
            }
        }.launchIn(lifecycleOwner.lifecycleScope + Dispatchers.Default)
    }

    override fun requestStatus() {
        commandOutputStream.send(
            mainRequest {
                subscribeOnConnectStatus = subscribeOnConnectStatusRequest { }
            }
        )
    }

    override fun getState(): StateFlow<ConnectStatusOuterClass.ConnectStatus> =
        connectionStatusStateFlow
}
