package com.flipperdevices.wearable.emulate.handheld.impl.request

import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
import com.flipperdevices.bridge.api.manager.ktx.state.FlipperSupportedState
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.SingleIn
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.wearable.emulate.common.WearableCommandInputStream
import com.flipperdevices.wearable.emulate.common.WearableCommandOutputStream
import com.flipperdevices.wearable.emulate.common.ipcemulate.Main
import com.flipperdevices.wearable.emulate.common.ipcemulate.mainResponse
import com.flipperdevices.wearable.emulate.common.ipcemulate.requests.ConnectStatusOuterClass
import com.flipperdevices.wearable.emulate.handheld.impl.di.WearHandheldGraph
import com.squareup.anvil.annotations.ContributesMultibinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@SingleIn(WearHandheldGraph::class)
@ContributesMultibinding(WearHandheldGraph::class, WearableCommandProcessor::class)
class WearableFlipperStatusProcessor @Inject constructor(
    private val commandInputStream: WearableCommandInputStream<Main.MainRequest>,
    private val commandOutputStream: WearableCommandOutputStream<Main.MainResponse>,
    private val scope: CoroutineScope,
    private val flipperServiceProvider: FlipperServiceProvider
) : WearableCommandProcessor, LogTagProvider {
    override val TAG: String = "WearableFlipperStatusProcessor-${hashCode()}"

    override fun init() {
        commandInputStream.getRequestsFlow().onEach {
            if (it.hasSubscribeOnConnectStatus()) {
                info { "SubscribeOnConnectStatus: ${it.subscribeOnConnectStatus}" }
                val connectionState = flipperServiceProvider.getServiceApi()
                    .connectionInformationApi
                    .getConnectionStateFlow().first()
                reportConnectionState(connectionState)
            }
        }.launchIn(scope)

        scope.launch(FlipperDispatchers.workStealingDispatcher) {
            flipperServiceProvider
                .getServiceApi()
                .connectionInformationApi
                .getConnectionStateFlow()
                .collect {
                    reportConnectionState(it)
                }
        }
    }

    private fun reportConnectionState(connectionState: ConnectionState) {
        val connectStatusProto = when (connectionState) {
            ConnectionState.Connecting -> ConnectStatusOuterClass.ConnectStatus.CONNECTING
            is ConnectionState.Disconnected -> ConnectStatusOuterClass.ConnectStatus.DISCONNECTED
            ConnectionState.Disconnecting -> ConnectStatusOuterClass.ConnectStatus.DISCONNECTING
            ConnectionState.Initializing -> ConnectStatusOuterClass.ConnectStatus.CONNECTING
            ConnectionState.RetrievingInformation ->
                ConnectStatusOuterClass.ConnectStatus.CONNECTING
            is ConnectionState.Ready ->
                if (connectionState.supportedState == FlipperSupportedState.READY) {
                    ConnectStatusOuterClass.ConnectStatus.READY
                } else {
                    ConnectStatusOuterClass.ConnectStatus.UNSUPPORTED
                }
        }

        commandOutputStream.send(
            mainResponse {
                connectStatus = connectStatusProto
            }
        )
    }
}
