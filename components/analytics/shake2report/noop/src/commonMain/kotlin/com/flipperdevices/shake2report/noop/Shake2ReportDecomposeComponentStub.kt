package com.flipperdevices.shake2report.noop

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.shake2report.api.Shake2ReportDecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import uk.kulikov.metro.assisted.ContributesAssistedFactory

@ContributesAssistedFactory(AppGraph::class, Shake2ReportDecomposeComponent.Factory::class)
@Suppress("UnusedPrivateProperty")
class Shake2ReportDecomposeComponentStub @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Suppress("UNUSED_PARAMETER")
    @Assisted onBack: DecomposeOnBackParameter
) : Shake2ReportDecomposeComponent(componentContext) {
    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render() = Unit
}
