package com.flipperdevices.debug.stresstest.fragments

import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
import com.flipperdevices.core.ui.ComposeFragment
import com.flipperdevices.debug.stresstest.composable.ComposableStressTestScreen
import com.flipperdevices.debug.stresstest.viewmodel.StressTestViewModel

@Deprecated(message = "Replace with compose navigation")
class StressTestFragment : ComposeFragment() {
    private val stressTestViewModel by viewModels<StressTestViewModel>()

    @Composable
    override fun RenderView() {
        ComposableStressTestScreen()
    }
}
