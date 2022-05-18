package com.flipperdevices.screenstreaming.impl.composable

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.screenstreaming.impl.model.StreamingState
import com.flipperdevices.screenstreaming.impl.viewmodel.ScreenStreamingViewModel

@Composable
fun ComposableStreamingScreen(
    screenStreamingViewModel: ScreenStreamingViewModel = viewModel()
) {
    ComposableScreen(
        screenStreamingViewModel,
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
