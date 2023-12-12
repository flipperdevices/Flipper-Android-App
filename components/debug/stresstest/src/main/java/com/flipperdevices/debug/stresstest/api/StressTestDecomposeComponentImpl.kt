package com.flipperdevices.debug.stresstest.api

import androidx.compose.runtime.Composable
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.ktx.viewModelWithFactory
import com.flipperdevices.debug.api.StressTestDecomposeComponent
import com.flipperdevices.debug.stresstest.composable.ComposableStressTestScreen
import com.flipperdevices.debug.stresstest.viewmodel.StressTestViewModel
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import javax.inject.Provider

@ContributesBinding(AppGraph::class, StressTestDecomposeComponent::class)
class StressTestDecomposeComponentImpl @Inject constructor(
    private val stressTestViewModelProvider: Provider<StressTestViewModel>
) : StressTestDecomposeComponent {
    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render() {
        val stressTestViewModel = viewModelWithFactory(key = null) {
            stressTestViewModelProvider.get()
        }
        ComposableStressTestScreen(
            viewModel = stressTestViewModel
        )
    }
}
