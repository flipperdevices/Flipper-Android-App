package com.flipperdevices.debug.stresstest.api

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.flipperdevices.debug.api.StressTestDecomposeComponent
import com.flipperdevices.debug.stresstest.composable.ComposableStressTestScreen
import com.flipperdevices.debug.stresstest.viewmodel.StressTestViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.gulya.anvil.assisted.ContributesAssistedFactory
import javax.inject.Provider

@ContributesAssistedFactory(AppGraph::class, StressTestDecomposeComponent.Factory::class)
class StressTestDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    private val stressTestViewModelProvider: Provider<StressTestViewModel>
) : StressTestDecomposeComponent(componentContext) {
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
