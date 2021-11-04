package com.flipperdevices.screenstreaming.impl.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ui.LifecycleViewModel
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.screen.Gui
import com.flipperdevices.protobuf.screen.sendInputEventRequest
import com.flipperdevices.protobuf.screen.startScreenStreamRequest
import com.flipperdevices.protobuf.screen.stopScreenStreamRequest
import com.flipperdevices.screenstreaming.impl.di.ScreenStreamingComponent
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ScreenStreamingViewModel : LifecycleViewModel() {
    @Inject
    lateinit var serviceProvider: FlipperServiceProvider

    private val flipperScreen = MutableStateFlow(ScreenStreamFrameDecoder.emptyBitmap())
    private var streamingActive = false
    private var serviceApi: FlipperServiceApi? = null

    init {
        ComponentHolder.component<ScreenStreamingComponent>().inject(this)
        serviceProvider.provideServiceApi(this) {
            serviceApi = it
            if (streamingActive) {
                onStartStreaming()
            }
        }
    }

    fun onStartStreaming() = viewModelScope.launch {
        serviceApi?.requestApi?.request(
            main {
                guiSendInputEventRequest = sendInputEventRequest {
                    key = Gui.InputKey.OK
                    type = Gui.InputType.SHORT
                }
            }
        )?.collect()
        serviceApi?.requestApi?.request(
            main {
                guiStartScreenStreamRequest = startScreenStreamRequest {}
            }
        )?.collect {
            onStreamFrameReceived(it.guiScreenStreamFrame)
        }
        streamingActive = true
    }

    private fun onStreamFrameReceived(streamFrame: Gui.ScreenStreamFrame) {
        val screen = ScreenStreamFrameDecoder.decode(streamFrame)
        viewModelScope.launch {
            flipperScreen.emit(screen)
        }
    }

    fun getFlipperScreen(): StateFlow<Bitmap> = flipperScreen

    fun onPauseStreaming() {
        serviceApi?.requestApi?.request(
            main {
                guiStopScreenStreamRequest = stopScreenStreamRequest {}
            }
        )
        streamingActive = false
    }
}
