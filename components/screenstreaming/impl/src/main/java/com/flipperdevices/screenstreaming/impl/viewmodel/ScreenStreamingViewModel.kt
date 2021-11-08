package com.flipperdevices.screenstreaming.impl.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ui.LifecycleViewModel
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.screen.Gui
import com.flipperdevices.protobuf.screen.startScreenStreamRequest
import com.flipperdevices.protobuf.screen.stopScreenStreamRequest
import com.flipperdevices.screenstreaming.impl.composable.ButtonEnum
import com.flipperdevices.screenstreaming.impl.di.ScreenStreamingComponent
import com.flipperdevices.screenstreaming.impl.model.StreamingState
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScreenStreamingViewModel : LifecycleViewModel() {
    @Inject
    lateinit var serviceProvider: FlipperServiceProvider

    private val flipperScreen = MutableStateFlow(ScreenStreamFrameDecoder.emptyBitmap())
    private var serviceApi: FlipperServiceApi? = null
    private val streamingState = MutableStateFlow(StreamingState.DISABLED)

    init {
        ComponentHolder.component<ScreenStreamingComponent>().inject(this)
        serviceProvider.provideServiceApi(this) { serviceApiInternal ->
            serviceApi = serviceApiInternal
            streamingState.onEach { state ->
                when (state) {
                    StreamingState.ENABLED -> onStartStreaming(serviceApiInternal)
                    StreamingState.DISABLED -> onPauseStreaming(serviceApiInternal)
                }
            }.launchIn(viewModelScope)
            serviceApiInternal.requestApi.notificationFlow().onEach {
                if (it.hasGuiScreenStreamFrame()) {
                    onStreamFrameReceived(it.guiScreenStreamFrame)
                }
            }.launchIn(viewModelScope)
        }
    }

    fun getFlipperScreen(): StateFlow<Bitmap> = flipperScreen
    fun getStreamingState() = streamingState

    fun onPressButton(buttonEnum: ButtonEnum) {
        serviceApi?.requestApi?.pressOnButton(viewModelScope, buttonEnum, Gui.InputType.SHORT)
    }

    fun onLongPressButton(buttonEnum: ButtonEnum) {
        serviceApi?.requestApi?.pressOnButton(viewModelScope, buttonEnum, Gui.InputType.LONG)
    }

    private fun onStartStreaming(serviceApi: FlipperServiceApi) {
        serviceApi.requestApi.request(
            main {
                guiStartScreenStreamRequest = startScreenStreamRequest {}
            }.wrapToRequest()
        ).onEach {
            onStreamFrameReceived(it.guiScreenStreamFrame)
        }.launchIn(viewModelScope)
    }

    private suspend fun onStreamFrameReceived(
        streamFrame: Gui.ScreenStreamFrame
    ) = withContext(Dispatchers.IO) {
        val screen = ScreenStreamFrameDecoder.decode(streamFrame)
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
}
