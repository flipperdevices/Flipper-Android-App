package com.flipperdevices.analytics.shake2report.impl.api

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.analytics.shake2report.impl.composable.Shake2ReportScreen
import com.flipperdevices.analytics.shake2report.impl.viewmodel.Shake2ReportViewModel
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.flipperdevices.shake2report.api.Shake2ReportDecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.squareup.anvil.annotations.ContributesBinding
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Provider

class Shake2ReportDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val onBack: DecomposeOnBackParameter,
    private val viewModelProvider: Provider<Shake2ReportViewModel>
) : Shake2ReportDecomposeComponent(componentContext) {
    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render() {
        Shake2ReportScreen(
            onBack = onBack::invoke,
            viewModel = viewModelWithFactory(key = null) {
                viewModelProvider.get()
            }
        )
    }

    @AssistedFactory
    @ContributesBinding(AppGraph::class, Shake2ReportDecomposeComponent.Factory::class)
    interface Factory : Shake2ReportDecomposeComponent.Factory {
        override operator fun invoke(
            componentContext: ComponentContext,
            onBack: DecomposeOnBackParameter
        ): Shake2ReportDecomposeComponentImpl
    }
}
