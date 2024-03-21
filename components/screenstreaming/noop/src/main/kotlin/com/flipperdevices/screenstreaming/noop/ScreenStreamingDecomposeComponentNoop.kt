package com.flipperdevices.screenstreaming.noop

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.screenstreaming.api.ScreenStreamingDecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.gulya.anvil.assisted.ContributesAssistedFactory

private data object MainScreen

@ContributesAssistedFactory(AppGraph::class, ScreenStreamingDecomposeComponent.Factory::class)
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
}
