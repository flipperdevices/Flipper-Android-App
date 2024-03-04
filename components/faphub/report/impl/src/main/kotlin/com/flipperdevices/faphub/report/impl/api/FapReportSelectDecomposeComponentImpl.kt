package com.flipperdevices.faphub.report.impl.api

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pushToFront
import com.flipperdevices.faphub.report.api.FapReportArgument
import com.flipperdevices.faphub.report.impl.composable.main.ComposableMainReport
import com.flipperdevices.faphub.report.impl.model.FapReportNavigationConfig
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class FapReportSelectDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val fapReportArgument: FapReportArgument,
    @Assisted private val navigation: StackNavigation<FapReportNavigationConfig>,
    @Assisted private val onBack: DecomposeOnBackParameter
) : ScreenDecomposeComponent(componentContext) {
    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render() {
        ComposableMainReport(
            onBack = onBack::invoke,
            onOpenBug = {
                navigation.pushToFront(
                    FapReportNavigationConfig.ReportBug(
                        reportUrl = fapReportArgument.reportUrl
                    )
                )
            },
            onOpenConcern = {
                navigation.pushToFront(
                    FapReportNavigationConfig.ReportConcern(
                        applicationUid = fapReportArgument.applicationUid
                    )
                )
            }
        )
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            fapReportArgument: FapReportArgument,
            navigation: StackNavigation<FapReportNavigationConfig>,
            onBack: DecomposeOnBackParameter
        ): FapReportSelectDecomposeComponentImpl
    }
}
