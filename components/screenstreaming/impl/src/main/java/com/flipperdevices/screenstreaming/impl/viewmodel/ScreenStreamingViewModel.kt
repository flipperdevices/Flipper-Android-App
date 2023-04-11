package com.flipperdevices.screenstreaming.impl.viewmodel

import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.ui.lifecycle.LifecycleViewModel
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.screen.Gui
import com.flipperdevices.protobuf.screen.startScreenStreamRequest
import com.flipperdevices.protobuf.screen.stopScreenStreamRequest
import com.flipperdevices.screenstreaming.impl.composable.ButtonEnum
import com.flipperdevices.screenstreaming.impl.model.FlipperButtonStack
import com.flipperdevices.screenstreaming.impl.model.FlipperScreenSnapshot
import com.flipperdevices.screenstreaming.impl.model.StreamingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tangle.viewmodel.VMInject

class ScreenStreamingViewModel @VMInject constructor(
    serviceProvider: FlipperServiceProvider,
    private val flipperButtonRequestHelper: FlipperButtonRequestHelper
) : LifecycleViewModel() {

    private val flipperScreen = MutableStateFlow(FlipperScreenSnapshot())
    private var serviceApi: FlipperServiceApi? = null
    private val streamingState = MutableStateFlow(StreamingState.DISABLED)
    private val stackFlipperButtons = MutableStateFlow(mutableListOf<FlipperButtonStack>())

    init {
        serviceProvider.provideServiceApi(this) { serviceApiInternal ->
            serviceApi = serviceApiInternal
            streamingState.onEach { state ->
                when (state) {
                    StreamingState.ENABLED -> onStartStreaming(serviceApiInternal)
                    StreamingState.DISABLED -> onPauseStreaming(serviceApiInternal)
                }
            }.launchIn(viewModelScope)
            serviceApiInternal.requestApi.notificationFlow().onEach {
                if (it.hasGuiScreenFrame()) {
                    onStreamFrameReceived(it.guiScreenFrame)
                }
            }.launchIn(viewModelScope)
        }
    }

    fun getFlipperScreen(): StateFlow<FlipperScreenSnapshot> = flipperScreen.asStateFlow()
    fun getFlipperButtons() = stackFlipperButtons.asStateFlow()

    fun onPressButton(
        buttonEnum: ButtonEnum,
        inputType: Gui.InputType
    ) {
        stackFlipperButtons.update {
            it.add(FlipperButtonStack(buttonEnum))
            it
        }
        flipperButtonRequestHelper.pressOnButton(
            viewModelScope = viewModelScope,
            key = buttonEnum.key,
            type = inputType,
            onComplete = {
                stackFlipperButtons.update {
                    it.removeAt(0)
                    it
                }
            }
        )
    }

    private fun onStartStreaming(serviceApi: FlipperServiceApi) {
        serviceApi.requestApi.request(
            main {
                guiStartScreenStreamRequest = startScreenStreamRequest {}
            }.wrapToRequest()
        ).onEach {
            onStreamFrameReceived(it.guiScreenFrame)
        }.launchIn(viewModelScope)
    }

    private suspend fun onStreamFrameReceived(
        streamFrame: Gui.ScreenFrame
    ) = withContext(Dispatchers.IO) {
        val screen = ScreenStreamFrameDecoder.decode(streamFrame) ?: return@withContext
        viewModelScope.launch {
            flipperScreen.emit(screen)
        }
    }

    private fun onPauseStreaming(serviceApi: FlipperServiceApi) {
        serviceApi.requestApi.request(
            main {
                guiStopScreenStreamRequest = stopScreenStreamRequest {}
            }.wrapToRequest()
        ).launchIn(viewModelScope)
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
