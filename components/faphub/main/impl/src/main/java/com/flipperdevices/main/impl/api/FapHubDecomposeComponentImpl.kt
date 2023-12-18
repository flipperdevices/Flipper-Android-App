package com.flipperdevices.main.impl.api

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
import com.flipperdevices.faphub.category.api.FapHubCategoryDecomposeComponent
import com.flipperdevices.faphub.fapscreen.api.FapScreenDecomposeComponent
import com.flipperdevices.faphub.main.api.FapHubDecomposeComponent
import com.flipperdevices.faphub.search.api.FapHubSearchDecomposeComponent
import com.flipperdevices.main.impl.model.FapHubNavigationConfig
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.squareup.anvil.annotations.ContributesBinding
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class FapHubDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    private val fapScreenFactory: FapScreenDecomposeComponent.Factory,
    private val fapSearchFactory: FapHubSearchDecomposeComponent.Factory,
    private val fapCategoryFactory: FapHubCategoryDecomposeComponent.Factory,
    private val mainScreenFactory: MainScreenDecomposeComponentImpl.Factory
) : FapHubDecomposeComponent, ComponentContext by componentContext {
    private val navigation = StackNavigation<FapHubNavigationConfig>()

    private val stack: Value<ChildStack<*, DecomposeComponent>> = childStack(
        source = navigation,
        serializer = FapHubNavigationConfig.serializer(),
        initialConfiguration = FapHubNavigationConfig.Main,
        handleBackButton = true,
        childFactory = ::child,
    )

    private fun child(
        config: FapHubNavigationConfig,
        componentContext: ComponentContext
    ): DecomposeComponent = when (config) {
        FapHubNavigationConfig.Main -> mainScreenFactory(
            componentContext = componentContext,
            navigation = navigation
        )

        is FapHubNavigationConfig.FapScreen -> fapScreenFactory(
            componentContext = componentContext,
            id = config.id
        )

        FapHubNavigationConfig.Search -> fapSearchFactory(
            componentContext = componentContext
        )

        is FapHubNavigationConfig.Category -> fapCategoryFactory(
            componentContext = componentContext,
            category = config.fapCategory
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
    @ContributesBinding(AppGraph::class, FapHubDecomposeComponent.Factory::class)
    interface Factory : FapHubDecomposeComponent.Factory {
        override fun invoke(
            componentContext: ComponentContext
        ): FapHubDecomposeComponentImpl
    }
}