package com.flipperdevices.screenstreaming.noop

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.screenstreaming.api.ScreenStreamingDecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import uk.kulikov.metro.assisted.ContributesAssistedFactory

private data object MainScreen

@ContributesAssistedFactory(AppGraph::class, ScreenStreamingDecomposeComponent.Factory::class)
class ScreenStreamingDecomposeComponentNoop @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted
    @Suppress("UnusedPrivateProperty", "UNUSED_PARAMETER")
    onBack: DecomposeOnBackParameter
) : ScreenStreamingDecomposeComponent(componentContext) {

    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render() {
        // Noop
    }
}
