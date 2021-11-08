package com.flipperdevices.screenstreaming.impl.fragment

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.fragment.app.viewModels
import com.flipperdevices.core.ui.ComposeFragment
import com.flipperdevices.screenstreaming.impl.composable.ComposableScreen
import com.flipperdevices.screenstreaming.impl.model.StreamingState
import com.flipperdevices.screenstreaming.impl.viewmodel.ScreenStreamingViewModel

class ScreenStreamingFragment : ComposeFragment() {
    private val screenStreamingViewModel by viewModels<ScreenStreamingViewModel>()

    @ExperimentalComposeUiApi
    @Composable
    override fun renderView() {
        val screen by screenStreamingViewModel.getFlipperScreen().collectAsState()
        val streamingState by screenStreamingViewModel.getStreamingState().collectAsState()
        ComposableScreen(
            flipperScreen = screen,
            streamingState = streamingState,
            onPressButton = { button ->
                screenStreamingViewModel.onPressButton(button)
            },
            onLongPressButton = { button ->
                screenStreamingViewModel.onLongPressButton(button)
            },
            onScreenStreamingSwitch = { state ->
                if (state == StreamingState.ENABLED) {
                    screenStreamingViewModel.getStreamingState().compareAndSet(
                        expect = StreamingState.DISABLED,
                        update = StreamingState.ENABLED
                    )
                } else if (state == StreamingState.DISABLED) {
                    screenStreamingViewModel.getStreamingState().compareAndSet(
                        expect = StreamingState.ENABLED,
                        update = StreamingState.DISABLED
                    )
                }
            }
        )
    }

    override fun onPause() {
        super.onPause()
        screenStreamingViewModel.getStreamingState().compareAndSet(
            expect = StreamingState.ENABLED,
            update = StreamingState.DISABLED
        )
    }
}
