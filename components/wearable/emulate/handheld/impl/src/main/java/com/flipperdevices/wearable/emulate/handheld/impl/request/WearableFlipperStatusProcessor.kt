package com.flipperdevices.wearable.emulate.handheld.impl.request

import com.flipperdevices.bridge.connection.feature.protocolversion.api.FVersionFeatureApi
import com.flipperdevices.bridge.connection.feature.protocolversion.model.FlipperSupportedState
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureStatus
import com.flipperdevices.bridge.connection.feature.provider.api.get
import com.flipperdevices.bridge.connection.orchestrator.api.FDeviceOrchestrator
import com.flipperdevices.bridge.connection.orchestrator.api.model.FDeviceConnectStatus
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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@SingleIn(WearHandheldGraph::class)
@ContributesMultibinding(WearHandheldGraph::class, WearableCommandProcessor::class)
class WearableFlipperStatusProcessor @Inject constructor(
    private val commandInputStream: WearableCommandInputStream<Main.MainRequest>,
    private val commandOutputStream: WearableCommandOutputStream<Main.MainResponse>,
    private val scope: CoroutineScope,
    private val fFeatureProvider: FFeatureProvider,
    private val fDeviceOrchestrator: FDeviceOrchestrator
) : WearableCommandProcessor, LogTagProvider {
    override val TAG: String = "WearableFlipperStatusProcessor-${hashCode()}"

    override fun init() {
        commandInputStream.getRequestsFlow().onEach {
            if (it.hasSubscribeOnConnectStatus()) {
                info { "SubscribeOnConnectStatus: ${it.subscribeOnConnectStatus}" }
                combine(
                    flow = fDeviceOrchestrator.getState(),
                    flow2 = fFeatureProvider.get<FVersionFeatureApi>()
                        .map { status -> status as? FFeatureStatus.Supported<FVersionFeatureApi> }
                        .map { status -> status?.featureApi }
                        .flatMapLatest { feature -> feature?.getSupportedStateFlow() ?: flowOf(null) },
                    transform = { connectionState, supportedState ->
                        reportConnectionState(connectionState, supportedState)
                    }
                ).first()
            }
        }.launchIn(scope)

        scope.launch(FlipperDispatchers.workStealingDispatcher) {
            combine(
                flow = fDeviceOrchestrator.getState(),
                flow2 = fFeatureProvider.get<FVersionFeatureApi>()
                    .map { status -> status as? FFeatureStatus.Supported<FVersionFeatureApi> }
                    .map { status -> status?.featureApi }
                    .flatMapLatest { feature -> feature?.getSupportedStateFlow() ?: flowOf(null) },
                transform = { connectionState, supportedState ->
                    reportConnectionState(connectionState, supportedState)
                }
            ).collect()
        }
    }

    private fun reportConnectionState(
        connectionState: FDeviceConnectStatus,
        supportedState: FlipperSupportedState?
    ) {
        val connectStatusProto = when (connectionState) {
            is FDeviceConnectStatus.Connecting -> ConnectStatusOuterClass.ConnectStatus.CONNECTING
            is FDeviceConnectStatus.Disconnecting -> ConnectStatusOuterClass.ConnectStatus.DISCONNECTING
            is FDeviceConnectStatus.Connected -> {
                if (supportedState == FlipperSupportedState.READY) {
                    ConnectStatusOuterClass.ConnectStatus.READY
                } else {
                    ConnectStatusOuterClass.ConnectStatus.UNSUPPORTED
                }
            }

            is FDeviceConnectStatus.Disconnected -> ConnectStatusOuterClass.ConnectStatus.DISCONNECTED
        }

        commandOutputStream.send(
            mainResponse {
                connectStatus = connectStatusProto
            }
        )
    }
}
