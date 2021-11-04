package com.flipperdevices.screenstreaming.impl.fragment

import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
import com.flipperdevices.core.ui.ComposeFragment
import com.flipperdevices.screenstreaming.impl.viewmodel.ScreenStreamingViewModel

class ScreenStreamingFragment : ComposeFragment() {
    private val screenStreamingViewModel by viewModels<ScreenStreamingViewModel>()

    @Composable
    override fun renderView() {
        // Do nothing
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
