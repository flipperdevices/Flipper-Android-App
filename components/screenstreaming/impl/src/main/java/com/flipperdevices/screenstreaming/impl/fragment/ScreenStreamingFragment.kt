package com.flipperdevices.screenstreaming.impl.fragment

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.fragment.app.viewModels
import com.flipperdevices.core.ui.ComposeFragment
import com.flipperdevices.screenstreaming.impl.composable.ComposableScreen
import com.flipperdevices.screenstreaming.impl.viewmodel.ScreenStreamingViewModel

class ScreenStreamingFragment : ComposeFragment() {
    private val screenStreamingViewModel by viewModels<ScreenStreamingViewModel>()

    @ExperimentalComposeUiApi
    @Composable
    override fun renderView() {
        val screen by screenStreamingViewModel.getFlipperScreen().collectAsState()
        ComposableScreen(screen) {
            screenStreamingViewModel.onPressButton(it)
        }
    }

    override fun onResume() {
        super.onResume()
        screenStreamingViewModel.onStartStreaming()
    }

    override fun onPause() {
        super.onPause()
        screenStreamingViewModel.onPauseStreaming()
    }
}
