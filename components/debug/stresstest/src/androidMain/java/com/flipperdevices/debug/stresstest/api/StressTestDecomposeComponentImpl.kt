package com.flipperdevices.debug.stresstest.api

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.flipperdevices.debug.api.StressTestDecomposeComponent
import com.flipperdevices.debug.stresstest.composable.ComposableStressTestScreen
import com.flipperdevices.debug.stresstest.viewmodel.StressTestViewModel
import com.squareup.anvil.annotations.ContributesBinding
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Provider

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

    @AssistedFactory
    @ContributesBinding(AppGraph::class, StressTestDecomposeComponent.Factory::class)
    interface Factory : StressTestDecomposeComponent.Factory {
        override fun invoke(
            componentContext: ComponentContext
        ): StressTestDecomposeComponentImpl
    }
}
