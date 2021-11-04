package com.flipperdevices.screenstreaming.impl.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ui.LifecycleViewModel
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.screen.Gui
import com.flipperdevices.protobuf.screen.sendInputEventRequest
import com.flipperdevices.protobuf.screen.startScreenStreamRequest
import com.flipperdevices.protobuf.screen.stopScreenStreamRequest
import com.flipperdevices.screenstreaming.impl.composable.ButtonEnum
import com.flipperdevices.screenstreaming.impl.di.ScreenStreamingComponent
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

    fun onStartStreaming() {
        serviceApi?.requestApi?.request(
            main {
                guiStartScreenStreamRequest = startScreenStreamRequest {}
            }
        )?.onEach {
            onStreamFrameReceived(it.guiScreenStreamFrame)
        }?.launchIn(viewModelScope)

        serviceApi?.requestApi?.notificationFlow()?.onEach {
            if (it.hasGuiScreenStreamFrame()) {
                onStreamFrameReceived(it.guiScreenStreamFrame)
            }
        }?.launchIn(viewModelScope)
        streamingActive = true
    }

    private fun FlipperRequestApi.sendInputEvent(
        buttonEnum: ButtonEnum,
        buttonType: Gui.InputType
    ) {
        request(
            main {
                guiSendInputEventRequest = sendInputEventRequest {
                    key = buttonEnum.key
                    type = buttonType
                }
            }
        ).launchIn(viewModelScope)
    }

    private fun FlipperRequestApi.pressOnButton(buttonEnum: ButtonEnum) {
        sendInputEvent(buttonEnum, Gui.InputType.PRESS)
        sendInputEvent(buttonEnum, Gui.InputType.SHORT)
        sendInputEvent(buttonEnum, Gui.InputType.RELEASE)
    }

    private suspend fun onStreamFrameReceived(
        streamFrame: Gui.ScreenStreamFrame
    ) = withContext(Dispatchers.IO) {
        val screen = ScreenStreamFrameDecoder.decode(streamFrame)
        viewModelScope.launch {
            flipperScreen.emit(screen)
        }
    }

    fun onPressButton(buttonEnum: ButtonEnum) {
        serviceApi?.requestApi?.pressOnButton(buttonEnum)
    }

    fun getFlipperScreen(): StateFlow<Bitmap> = flipperScreen

    fun onPauseStreaming() {
        serviceApi?.requestApi?.request(
            main {
                guiStopScreenStreamRequest = stopScreenStreamRequest {}
            }
        )?.launchIn(viewModelScope)
        streamingActive = false
    }
}
