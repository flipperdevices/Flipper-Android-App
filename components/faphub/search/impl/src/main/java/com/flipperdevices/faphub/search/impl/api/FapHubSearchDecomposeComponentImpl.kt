package com.flipperdevices.faphub.search.impl.api

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
import com.flipperdevices.faphub.search.api.FapHubSearchDecomposeComponent
import com.flipperdevices.faphub.search.impl.model.FapHubSearchNavigationConfig
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.squareup.anvil.annotations.ContributesBinding
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class FapHubSearchDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    private val fapScreenFactory: FapScreenDecomposeComponent.Factory,
    private val searchScreenFactory: SearchScreenDecomposeComponentImpl.Factory
) : FapHubSearchDecomposeComponent, ComponentContext by componentContext {
    private val navigation = StackNavigation<FapHubSearchNavigationConfig>()

    private val stack: Value<ChildStack<*, DecomposeComponent>> = childStack(
        source = navigation,
        serializer = FapHubSearchNavigationConfig.serializer(),
        initialConfiguration = FapHubSearchNavigationConfig.SearchScreen,
        handleBackButton = true,
        childFactory = ::child,
    )

    private fun child(
        config: FapHubSearchNavigationConfig,
        componentContext: ComponentContext
    ): DecomposeComponent = when (config) {
        is FapHubSearchNavigationConfig.FapScreen -> fapScreenFactory(
            componentContext = componentContext,
            id = config.id
        )

        FapHubSearchNavigationConfig.SearchScreen -> searchScreenFactory(
            componentContext = componentContext,
            navigation = navigation
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
    @ContributesBinding(AppGraph::class, FapHubSearchDecomposeComponent.Factory::class)
    interface Factory : FapHubSearchDecomposeComponent.Factory {
        override fun invoke(
            componentContext: ComponentContext
        ): FapHubSearchDecomposeComponentImpl
    }
}
