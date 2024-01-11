package com.flipperdevices.shake2report.noop

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.shake2report.api.Shake2ReportDecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.squareup.anvil.annotations.ContributesBinding
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

@Suppress("UnusedPrivateProperty")
class Shake2ReportDecomposeComponentStub @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted onBack: DecomposeOnBackParameter
) : Shake2ReportDecomposeComponent(), ComponentContext by componentContext {
    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render() = Unit

    @AssistedFactory
    @ContributesBinding(AppGraph::class, Shake2ReportDecomposeComponent.Factory::class)
    interface Factory : Shake2ReportDecomposeComponent.Factory {
        override operator fun invoke(
            componentContext: ComponentContext,
            onBack: DecomposeOnBackParameter
        ): Shake2ReportDecomposeComponentStub
    }
}
