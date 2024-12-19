package com.flipperdevices.screenstreaming.impl.viewmodel.repository

import com.arkivanov.essenty.lifecycle.Lifecycle
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureStatus
import com.flipperdevices.bridge.connection.feature.provider.api.get
import com.flipperdevices.bridge.connection.feature.provider.api.getSync
import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.screenstreaming.api.FScreenStreamingFeatureApi
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.protobuf.screen.ScreenFrame
import com.flipperdevices.screenstreaming.impl.model.FlipperScreenState
import com.flipperdevices.screenstreaming.impl.model.StreamingState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class StreamingRepository(
    private val scope: CoroutineScope,
    private val fFeatureProvider: FFeatureProvider
) : Lifecycle.Callbacks, LogTagProvider {
    override val TAG: String = "StreamingRepository"

    private val flipperScreen = MutableStateFlow<FlipperScreenState>(FlipperScreenState.InProgress)
    private val streamingStateFlow = MutableStateFlow(StreamingState.DISABLED)
    private var startStreamingJob: Job? = null
    private var startStreamingMutex: Mutex = Mutex()

    fun getFlipperScreen() = flipperScreen.asStateFlow()

    private suspend fun onStreamFrameReceived(
        streamFrame: ScreenFrame
    ) = withContext(FlipperDispatchers.workStealingDispatcher) {
        val screen = ScreenStreamFrameDecoder.decode(streamFrame) ?: run {
            error { "#onStreamFrameReceived could not decode streamFrame" }
            return@withContext
        }
        scope.launch {
            flipperScreen.emit(screen)
        }
    }

    private fun onStartStreaming(fScreenStreamingFeatureApi: FScreenStreamingFeatureApi) {
        scope.launch {
            startStreamingJob?.cancelAndJoin()
            startStreamingMutex.withLock {
                startStreamingJob = scope.launch {
                    fScreenStreamingFeatureApi.guiScreenFrameFlow()
                        .onEach { guiScreenFrame -> onStreamFrameReceived(guiScreenFrame) }
                        .collect()
                }
                startStreamingJob?.join()
            }
        }
    }

    private fun onPauseStreaming(fScreenStreamingFeatureApi: FScreenStreamingFeatureApi) {
        scope.launch {
            fScreenStreamingFeatureApi.stop()
                .onFailure { error(it) { "#onPauseStreaming could not stop streaming" } }
        }
    }

    init {
        fFeatureProvider.get<FRpcFeatureApi>()
            .filterIsInstance<FFeatureStatus.Supported<FRpcFeatureApi>>()
            .flatMapLatest { status -> status.featureApi.notificationFlow() }
            .mapNotNull { main -> main.gui_screen_frame }
            .onEach { guiScreenFrame -> onStreamFrameReceived(guiScreenFrame) }
            .launchIn(scope)

        combine(
            flow = fFeatureProvider.get<FScreenStreamingFeatureApi>(),
            flow2 = streamingStateFlow,
            transform = { status, streamingState ->
                when (status) {
                    FFeatureStatus.Unsupported,
                    FFeatureStatus.NotFound -> {
                        flipperScreen.emit(FlipperScreenState.NotConnected)
                    }

                    FFeatureStatus.Retrieving -> {
                        flipperScreen.emit(FlipperScreenState.InProgress)
                    }

                    is FFeatureStatus.Supported -> {
                        when (streamingState) {
                            StreamingState.ENABLED -> onStartStreaming(status.featureApi)
                            StreamingState.DISABLED -> onPauseStreaming(status.featureApi)
                        }
                    }
                }
            }
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

    override fun onDestroy() {
        scope.launch(NonCancellable) {
            fFeatureProvider.getSync<FScreenStreamingFeatureApi>()?.stop()
        }
    }
}
