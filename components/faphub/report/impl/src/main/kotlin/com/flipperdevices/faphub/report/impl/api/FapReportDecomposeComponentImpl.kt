package com.flipperdevices.faphub.report.impl.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.faphub.report.api.FapReportArgument
import com.flipperdevices.faphub.report.api.FapReportDecomposeComponent
import com.flipperdevices.faphub.report.impl.model.FapReportNavigationConfig
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.popOr
import com.squareup.anvil.annotations.ContributesBinding
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class FapReportDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted fapReportArgument: FapReportArgument,
    @Assisted private val onBack: DecomposeOnBackParameter,
    private val reportSelectFactory: FapReportSelectDecomposeComponentImpl.Factory,
    private val reportConcernFactory: FapReportConcernDecomposeComponentImpl.Factory,
    private val reportBugFactory: FapReportBugDecomposeComponentImpl.Factory
) : FapReportDecomposeComponent, ComponentContext by componentContext {
    private val navigation = StackNavigation<FapReportNavigationConfig>()

    private val stack: Value<ChildStack<*, DecomposeComponent>> = childStack(
        source = navigation,
        serializer = FapReportNavigationConfig.serializer(),
        initialConfiguration = FapReportNavigationConfig.ReportSelect(fapReportArgument),
        handleBackButton = true,
        childFactory = ::child,
    )

    private fun child(
        config: FapReportNavigationConfig,
        componentContext: ComponentContext
    ): DecomposeComponent = when (config) {
        is FapReportNavigationConfig.ReportBug -> reportBugFactory(
            componentContext = componentContext,
            reportUrl = config.reportUrl,
            onBack = { navigation.popOr(onBack::invoke) }
        )

        is FapReportNavigationConfig.ReportConcern -> reportConcernFactory(
            componentContext = componentContext,
            applicationUid = config.applicationUid,
            onBack = { navigation.popOr(onBack::invoke) }
        )

        is FapReportNavigationConfig.ReportSelect -> reportSelectFactory(
            componentContext = componentContext,
            fapReportArgument = config.fapReportArgument,
            navigation = navigation,
            onBack = { navigation.popOr(onBack::invoke) }
        )
    }

    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render() {
        val childStack by stack.subscribeAsState()

        Children(
            stack = childStack,
        ) {
            it.instance.Render()
        }
    }

    @AssistedFactory
    @ContributesBinding(AppGraph::class, FapReportDecomposeComponent.Factory::class)
    interface Factory : FapReportDecomposeComponent.Factory {
        override fun invoke(
            componentContext: ComponentContext,
            fapReportArgument: FapReportArgument,
            onBack: DecomposeOnBackParameter
        ): FapReportDecomposeComponentImpl
    }
}
