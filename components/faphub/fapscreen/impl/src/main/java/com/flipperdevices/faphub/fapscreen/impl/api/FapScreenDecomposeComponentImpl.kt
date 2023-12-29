package com.flipperdevices.faphub.fapscreen.impl.api

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
import com.flipperdevices.faphub.fapscreen.api.FapScreenDecomposeComponent
import com.flipperdevices.faphub.fapscreen.impl.model.FapScreenNavigationConfig
import com.flipperdevices.faphub.report.api.FapReportDecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.popOr
import com.squareup.anvil.annotations.ContributesBinding
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

@Suppress("LongParameterList")
class FapScreenDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val id: String,
    @Assisted private val onBack: DecomposeOnBackParameter,
    private val fapReportFactory: FapReportDecomposeComponent.Factory,
    private val screenDecomposeFactory: ScreenDecomposeComponentImpl.Factory
) : FapScreenDecomposeComponent, ComponentContext by componentContext {
    private val navigation = StackNavigation<FapScreenNavigationConfig>()

    private val stack: Value<ChildStack<*, DecomposeComponent>> = childStack(
        source = navigation,
        serializer = FapScreenNavigationConfig.serializer(),
        initialConfiguration = FapScreenNavigationConfig.Main(id),
        handleBackButton = true,
        childFactory = ::child,
    )

    private fun child(
        config: FapScreenNavigationConfig,
        componentContext: ComponentContext
    ): DecomposeComponent = when (config) {
        is FapScreenNavigationConfig.Main -> screenDecomposeFactory(
            componentContext = componentContext,
            id = config.id,
            navigation = navigation,
            onBack = { navigation.popOr(onBack::invoke) }
        )

        is FapScreenNavigationConfig.FapReport -> fapReportFactory(
            componentContext = componentContext,
            fapReportArgument = config.fapReportArgument,
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
    @ContributesBinding(AppGraph::class, FapScreenDecomposeComponent.Factory::class)
    interface Factory : FapScreenDecomposeComponent.Factory {
        override fun invoke(
            componentContext: ComponentContext,
            id: String,
            onBack: DecomposeOnBackParameter
        ): FapScreenDecomposeComponentImpl
    }
}
