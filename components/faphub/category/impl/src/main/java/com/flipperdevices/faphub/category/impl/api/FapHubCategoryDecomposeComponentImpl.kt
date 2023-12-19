package com.flipperdevices.faphub.category.impl.api

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
import com.flipperdevices.faphub.category.impl.model.FapCategoryNavigationConfig
import com.flipperdevices.faphub.dao.api.model.FapCategory
import com.flipperdevices.faphub.fapscreen.api.FapScreenDecomposeComponent
import com.flipperdevices.faphub.search.api.FapHubSearchDecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.squareup.anvil.annotations.ContributesBinding
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class FapHubCategoryDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val fapCategory: FapCategory,
    private val fapScreenFactory: FapScreenDecomposeComponent.Factory,
    private val fapSearchFactory: FapHubSearchDecomposeComponent.Factory,
    private val fapCategoryScreenFactory: FapHubCategoryScreenDecomposeComponentImpl.Factory
) : FapHubCategoryDecomposeComponent, ComponentContext by componentContext {
    private val navigation = StackNavigation<FapCategoryNavigationConfig>()

    private val stack: Value<ChildStack<*, DecomposeComponent>> = childStack(
        source = navigation,
        serializer = FapCategoryNavigationConfig.serializer(),
        initialConfiguration = FapCategoryNavigationConfig.CategoryList(fapCategory),
        handleBackButton = true,
        childFactory = ::child,
    )

    private fun child(
        config: FapCategoryNavigationConfig,
        componentContext: ComponentContext
    ): DecomposeComponent = when (config) {
        is FapCategoryNavigationConfig.CategoryList -> fapCategoryScreenFactory(
            componentContext = componentContext,
            fapCategory = config.fapCategory,
            navigation = navigation
        )

        is FapCategoryNavigationConfig.FapScreen -> fapScreenFactory(
            componentContext = componentContext,
            id = config.id
        )

        FapCategoryNavigationConfig.Search -> fapSearchFactory(
            componentContext = componentContext
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
    @ContributesBinding(AppGraph::class, FapHubCategoryDecomposeComponent.Factory::class)
    interface Factory : FapHubCategoryDecomposeComponent.Factory {
        override fun invoke(
            componentContext: ComponentContext,
            category: FapCategory
        ): FapHubCategoryDecomposeComponentImpl
    }
}
