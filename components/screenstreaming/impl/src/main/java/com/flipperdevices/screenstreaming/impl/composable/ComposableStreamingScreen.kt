package com.flipperdevices.screenstreaming.impl.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
    val lifecycleState by LocalLifecycleOwner.current.lifecycle.observeAsState()
    when (lifecycleState) {
        Lifecycle.Event.ON_PAUSE -> screenStreamingViewModel.disableStreaming()
        else -> {}
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
            when (state) {
                StreamingState.ENABLED -> screenStreamingViewModel.enableStreaming()
                StreamingState.DISABLED -> screenStreamingViewModel.disableStreaming()
            }
        }
    )
}
