package com.flipperdevices.screenstreaming.impl.viewmodel.repository

import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
import com.flipperdevices.bridge.api.manager.ktx.state.FlipperSupportedState
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.screen.Gui
import com.flipperdevices.protobuf.screen.startScreenStreamRequest
import com.flipperdevices.protobuf.screen.stopScreenStreamRequest
import com.flipperdevices.screenstreaming.impl.model.FlipperScreenState
import com.flipperdevices.screenstreaming.impl.model.StreamingState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StreamingRepository(
    private val scope: CoroutineScope
) : FlipperBleServiceConsumer {
    private val flipperScreen = MutableStateFlow<FlipperScreenState>(FlipperScreenState.InProgress)
    private val streamingState = MutableStateFlow(StreamingState.DISABLED)

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) {
        streamingState.onEach { state ->
            when (state) {
                StreamingState.ENABLED -> onStartStreaming(serviceApi)
                StreamingState.DISABLED -> onPauseStreaming(serviceApi)
            }
        }.launchIn(scope)
        serviceApi.requestApi.notificationFlow().onEach {
            if (it.hasGuiScreenFrame()) {
                onStreamFrameReceived(it.guiScreenFrame)
            }
        }.launchIn(scope)
        serviceApi
            .connectionInformationApi
            .getConnectionStateFlow().onEach {
                if (it is ConnectionState.Ready &&
                    it.supportedState == FlipperSupportedState.READY
                ) {
                    flipperScreen.emit(FlipperScreenState.InProgress)
                } else {
                    flipperScreen.emit(FlipperScreenState.NotConnected)
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
    ) = withContext(Dispatchers.IO) {
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

    fun enableStreaming() {
        streamingState.compareAndSet(
            expect = StreamingState.DISABLED,
            update = StreamingState.ENABLED
        )
    }

    fun disableStreaming() {
        streamingState.compareAndSet(
            expect = StreamingState.ENABLED,
            update = StreamingState.DISABLED
        )
    }
}
