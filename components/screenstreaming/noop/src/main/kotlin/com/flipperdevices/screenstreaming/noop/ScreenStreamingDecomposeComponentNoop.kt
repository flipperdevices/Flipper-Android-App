package com.flipperdevices.screenstreaming.noop

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.screenstreaming.api.ScreenStreamingDecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

class ScreenStreamingDecomposeComponentNoop @Inject constructor() :
    ScreenStreamingDecomposeComponent {

    @Composable
    override fun Render() {
        // Noop
    }

    @ContributesBinding(AppGraph::class, ScreenStreamingDecomposeComponent.Factory::class)
    interface Factory : ScreenStreamingDecomposeComponent.Factory {
        override fun invoke(
            componentContext: ComponentContext,
            onBack: DecomposeOnBackParameter
        ): ScreenStreamingDecomposeComponentNoop
    }
}
