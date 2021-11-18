package com.flipperdevices.debug.impl.fragments

import androidx.compose.runtime.Composable
import com.flipperdevices.core.ui.ComposeFragment
import com.flipperdevices.debug.impl.compose.ComposableDebugScreen

class DebugScreenFragment : ComposeFragment() {
    @Composable
    override fun RenderView() {
        ComposableDebugScreen()
    }
}
