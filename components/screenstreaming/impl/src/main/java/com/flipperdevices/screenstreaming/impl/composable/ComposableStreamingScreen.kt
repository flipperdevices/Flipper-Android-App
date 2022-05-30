package com.flipperdevices.screenstreaming.impl.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.core.ktx.android.observeAsState
import com.flipperdevices.screenstreaming.impl.model.StreamingState
import com.flipperdevices.screenstreaming.impl.viewmodel.ScreenStreamingViewModel

@Composable
fun ComposableStreamingScreen(
    screenStreamingViewModel: ScreenStreamingViewModel = viewModel()
) {
    val lifecycleState = LocalLifecycleOwner.current.lifecycle.observeAsState()
    if (lifecycleState.value == Lifecycle.Event.ON_PAUSE) {
        screenStreamingViewModel.getStreamingState().compareAndSet(
            expect = StreamingState.ENABLED,
            update = StreamingState.DISABLED
        )
    }

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
