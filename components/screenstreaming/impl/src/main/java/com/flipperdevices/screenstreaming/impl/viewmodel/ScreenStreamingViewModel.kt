package com.flipperdevices.screenstreaming.impl.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ktx.jre.createClearNewFileWithMkDirs
import com.flipperdevices.core.share.SharableFile
import com.flipperdevices.core.share.ShareHelper
import com.flipperdevices.core.ui.lifecycle.LifecycleViewModel
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.screen.Gui
import com.flipperdevices.protobuf.screen.startScreenStreamRequest
import com.flipperdevices.protobuf.screen.stopScreenStreamRequest
import com.flipperdevices.screenstreaming.impl.R
import com.flipperdevices.screenstreaming.impl.composable.ButtonEnum
import com.flipperdevices.screenstreaming.impl.di.ScreenStreamingComponent
import com.flipperdevices.screenstreaming.impl.model.FlipperScreenSnapshot
import com.flipperdevices.screenstreaming.impl.model.StreamingState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tangle.viewmodel.VMInject

private const val SCREENSHOT_FILE_PREFIX = "flpr"
private const val TIMEFORMAT = "yyyy-MM-dd-HH:mm:ss"
private const val QUALITY = 100

class ScreenStreamingViewModel @VMInject constructor(
    serviceProvider: FlipperServiceProvider,
    private val application: Application
) : LifecycleViewModel() {

    private val flipperScreen = MutableStateFlow(FlipperScreenSnapshot())
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
                if (it.hasGuiScreenFrame()) {
                    onStreamFrameReceived(it.guiScreenFrame)
                }
            }.launchIn(viewModelScope)
        }
    }

    fun getFlipperScreen(): StateFlow<FlipperScreenSnapshot> = flipperScreen

    fun onPressButton(buttonEnum: ButtonEnum) = when (buttonEnum) {
        ButtonEnum.UNLOCK -> serviceApi?.requestApi?.pressOnButton(
            viewModelScope,
            Gui.InputKey.BACK,
            Gui.InputType.SHORT,
            times = 3
        )
        ButtonEnum.SCREENSHOT -> shareScreenshot()
        ButtonEnum.LEFT,
        ButtonEnum.RIGHT,
        ButtonEnum.UP,
        ButtonEnum.DOWN,
        ButtonEnum.OK,
        ButtonEnum.BACK -> buttonEnum.key?.let { key ->
            serviceApi?.requestApi?.pressOnButton(viewModelScope, key, Gui.InputType.SHORT)
        }
    }

    fun onLongPressButton(buttonEnum: ButtonEnum) {
        val key = buttonEnum.key ?: return
        serviceApi?.requestApi?.pressOnButton(viewModelScope, key, Gui.InputType.LONG)
    }

    private fun shareScreenshot() = lifecycleScope.launch(Dispatchers.Default) {
        val currentSnapshot = flipperScreen.value.bitmap ?: return@launch
        val date = SimpleDateFormat(TIMEFORMAT, Locale.US).format(Date())
        val filename = "$SCREENSHOT_FILE_PREFIX-$date.png"
        val sharableFile = SharableFile(application, filename)
        sharableFile.createClearNewFileWithMkDirs()
        sharableFile.outputStream().use {
            currentSnapshot.compress(Bitmap.CompressFormat.PNG, QUALITY, it)
        }
        ShareHelper.shareFile(application, sharableFile, R.string.screenshot_export_title)
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
        val screen = ScreenStreamFrameDecoder.decode(streamFrame)
        viewModelScope.launch {
            flipperScreen.emit(FlipperScreenSnapshot(screen))
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
