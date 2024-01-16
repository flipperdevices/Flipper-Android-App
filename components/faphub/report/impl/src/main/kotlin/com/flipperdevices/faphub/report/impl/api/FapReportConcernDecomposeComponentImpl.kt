package com.flipperdevices.faphub.report.impl.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.flipperdevices.faphub.report.impl.composable.concern.ComposableReport
import com.flipperdevices.faphub.report.impl.viewmodel.ReportViewModel
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class FapReportConcernDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val applicationUid: String,
    @Assisted private val onBack: DecomposeOnBackParameter,
    private val reportViewModelFactory: ReportViewModel.Factory
) : ScreenDecomposeComponent(componentContext) {
    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render() {
        val reportViewModel = viewModelWithFactory(key = applicationUid) {
            reportViewModelFactory(applicationUid)
        }
        val state by reportViewModel.getFapReportState().collectAsState()
        ComposableReport(
            onBack = onBack::invoke,
            fapReportState = state,
            submit = { reportViewModel.submit(onBack::invoke, it) }
        )
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            applicationUid: String,
            onBack: DecomposeOnBackParameter
        ): FapReportConcernDecomposeComponentImpl
    }
}
