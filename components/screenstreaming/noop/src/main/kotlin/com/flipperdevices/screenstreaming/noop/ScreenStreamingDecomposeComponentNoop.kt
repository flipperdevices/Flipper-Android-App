package com.flipperdevices.screenstreaming.noop

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.screenstreaming.api.ScreenStreamingDecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.squareup.anvil.annotations.ContributesBinding
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

private data object MainScreen

class ScreenStreamingDecomposeComponentNoop @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted
    @Suppress("UnusedPrivateProperty")
    onBack: DecomposeOnBackParameter
) : ScreenStreamingDecomposeComponent(componentContext) {

    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render() {
        // Noop
    }

    @AssistedFactory
    @ContributesBinding(AppGraph::class, ScreenStreamingDecomposeComponent.Factory::class)
    interface Factory : ScreenStreamingDecomposeComponent.Factory {
        override fun invoke(
            componentContext: ComponentContext,
            onBack: DecomposeOnBackParameter
        ): ScreenStreamingDecomposeComponentNoop
    }
}
