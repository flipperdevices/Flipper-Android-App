package com.flipperdevices.screenstreaming.impl.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import com.flipperdevices.core.ktx.android.observeAsState
import com.flipperdevices.screenstreaming.impl.viewmodel.ScreenStreamingViewModel
import tangle.viewmodel.compose.tangleViewModel

@Composable
fun ComposableStreamingScreen() {
    val screenStreamingViewModel: ScreenStreamingViewModel = tangleViewModel()
    val lifecycleState by LocalLifecycleOwner.current.lifecycle.observeAsState()
    when (lifecycleState) {
        Lifecycle.Event.ON_RESUME -> screenStreamingViewModel.enableStreaming()
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
        }
    )
}
