package com.flipperdevices.shake2report.noop

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.shake2report.api.Shake2ReportDecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.gulya.anvil.assisted.ContributesAssistedFactory

@ContributesAssistedFactory(AppGraph::class, Shake2ReportDecomposeComponent.Factory::class)
@Suppress("UnusedPrivateProperty")
class Shake2ReportDecomposeComponentStub @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted onBack: DecomposeOnBackParameter
) : Shake2ReportDecomposeComponent(componentContext) {
    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render() = Unit
}
