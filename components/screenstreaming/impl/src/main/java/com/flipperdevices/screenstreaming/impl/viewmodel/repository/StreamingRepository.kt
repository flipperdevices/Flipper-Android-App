package com.flipperdevices.screenstreaming.impl.viewmodel.repository

import com.arkivanov.essenty.lifecycle.Lifecycle
import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
import com.flipperdevices.bridge.api.manager.ktx.state.FlipperSupportedState
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.screen.Gui
import com.flipperdevices.protobuf.screen.startScreenStreamRequest
import com.flipperdevices.protobuf.screen.stopScreenStreamRequest
import com.flipperdevices.screenstreaming.impl.model.FlipperScreenState
import com.flipperdevices.screenstreaming.impl.model.StreamingState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StreamingRepository(
    private val scope: CoroutineScope
) : FlipperBleServiceConsumer, Lifecycle.Callbacks {
    private val flipperScreen = MutableStateFlow<FlipperScreenState>(FlipperScreenState.InProgress)
    private val streamingStateFlow = MutableStateFlow(StreamingState.DISABLED)

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) {
        combine(
            streamingStateFlow,
            serviceApi
                .connectionInformationApi
                .getConnectionStateFlow()
        ) { streamingState, connectionState ->
            if (connectionState is ConnectionState.Ready &&
                connectionState.supportedState == FlipperSupportedState.READY
            ) {
                flipperScreen.emit(FlipperScreenState.InProgress)
                when (streamingState) {
                    StreamingState.ENABLED -> onStartStreaming(serviceApi)
                    StreamingState.DISABLED -> onPauseStreaming(serviceApi)
                }
            } else {
                flipperScreen.emit(FlipperScreenState.NotConnected)
            }
        }.launchIn(scope)
        serviceApi.requestApi.notificationFlow().onEach {
            if (it.hasGuiScreenFrame()) {
                onStreamFrameReceived(it.guiScreenFrame)
            }
        }.launchIn(scope)
    }

    fun getFlipperScreen() = flipperScreen.asStateFlow()

    private fun onStartStreaming(serviceApi: FlipperServiceApi) {
        serviceApi.requestApi.request(
            main {
                guiStartScreenStreamRequest = startScreenStreamRequest {}
            }.wrapToRequest()
        ).onEach {
            onStreamFrameReceived(it.guiScreenFrame)
        }.launchIn(scope)
    }

    private suspend fun onStreamFrameReceived(
        streamFrame: Gui.ScreenFrame
    ) = withContext(FlipperDispatchers.workStealingDispatcher) {
        val screen = ScreenStreamFrameDecoder.decode(streamFrame) ?: return@withContext
        scope.launch {
            flipperScreen.emit(screen)
        }
    }

    private fun onPauseStreaming(serviceApi: FlipperServiceApi) {
        serviceApi.requestApi.request(
            main {
                guiStopScreenStreamRequest = stopScreenStreamRequest {}
            }.wrapToRequest()
        ).launchIn(scope)
    }

    override fun onResume() {
        streamingStateFlow.compareAndSet(
            expect = StreamingState.DISABLED,
            update = StreamingState.ENABLED
        )
    }

    override fun onPause() {
        streamingStateFlow.compareAndSet(
            expect = StreamingState.ENABLED,
            update = StreamingState.DISABLED
        )
    }
}
