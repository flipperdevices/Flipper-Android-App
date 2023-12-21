package com.flipperdevices.archive.search.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.flipperdevices.archive.api.SearchDecomposeComponent
import com.flipperdevices.archive.api.SelectKeyPathListener
import com.flipperdevices.archive.search.model.SearchNavigationConfig
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.keyscreen.api.KeyScreenDecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.squareup.anvil.annotations.ContributesBinding
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class SearchDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted onItemSelected: SelectKeyPathListener?,
    private val keyScreenFactory: KeyScreenDecomposeComponent.Factory,
    private val searchScreenFactory: SearchScreenDecomposeComponentImpl.Factory
) : SearchDecomposeComponent, ComponentContext by componentContext {
    private val navigation = StackNavigation<SearchNavigationConfig>()

    private val stack: Value<ChildStack<*, DecomposeComponent>> = childStack(
        source = navigation,
        serializer = SearchNavigationConfig.serializer(),
        initialConfiguration = SearchNavigationConfig.Search(onItemSelected),
        handleBackButton = true,
        childFactory = ::child,
    )

    private fun child(
        config: SearchNavigationConfig,
        componentContext: ComponentContext
    ): DecomposeComponent = when (config) {
        is SearchNavigationConfig.OpenKey -> keyScreenFactory(
            componentContext = componentContext,
            keyPath = config.keyPath
        )

        is SearchNavigationConfig.Search -> searchScreenFactory(
            componentContext = componentContext,
            onItemSelected = config.onItemSelected,
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
    @ContributesBinding(AppGraph::class, SearchDecomposeComponent.Factory::class)
    interface Factory : SearchDecomposeComponent.Factory {
        override fun invoke(
            componentContext: ComponentContext,
            onItemSelected: SelectKeyPathListener?
        ): SearchDecomposeComponentImpl
    }
}
