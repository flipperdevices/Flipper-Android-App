package com.flipperdevices.screenstreaming.impl.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.flipperdevices.screenstreaming.impl.viewmodel.ScreenStreamingViewModel
import tangle.viewmodel.compose.tangleViewModel

@Composable
fun ComposableStreamingScreen() {
    val screenStreamingViewModel: ScreenStreamingViewModel = tangleViewModel()
    DisposableEffect(key1 = Unit) {
        screenStreamingViewModel.enableStreaming()
        onDispose {
            screenStreamingViewModel.disableStreaming()
        }
    }

    ComposableScreen(
        screenStreamingViewModel,
        onPressButton = { button ->
            screenStreamingViewModel.onPressButton(button)
        },
        onLongPressButton = { button ->
            screenStreamingViewModel.onLongPressButton(button)
        }
    )
}
