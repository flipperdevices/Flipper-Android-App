package com.flipperdevices.firstpair.impl.fragments

import androidx.compose.runtime.Composable
import com.flipperdevices.core.ui.ComposeFragment
import com.flipperdevices.firstpair.impl.composable.searching.ComposableSearchingScreen

class DeviceSearchingFragment : ComposeFragment() {
    @Composable
    override fun RenderView() {
        ComposableSearchingScreen()
    }
}
