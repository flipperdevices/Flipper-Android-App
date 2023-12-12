package com.flipperdevices.shake2report.noop

import androidx.compose.runtime.Composable
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.shake2report.api.Shake2ReportDecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.squareup.anvil.annotations.ContributesBinding
import dagger.assisted.AssistedFactory
import javax.inject.Inject

class Shake2ReportDecomposeComponentStub @Inject constructor() : Shake2ReportDecomposeComponent {
    @Composable
    override fun Render() = Unit

    @AssistedFactory
    @ContributesBinding(AppGraph::class, Shake2ReportDecomposeComponent.Factory::class)
    interface Factory : Shake2ReportDecomposeComponent.Factory {
        override operator fun invoke(
            onBack: DecomposeOnBackParameter
        ): Shake2ReportDecomposeComponentStub
    }
}
