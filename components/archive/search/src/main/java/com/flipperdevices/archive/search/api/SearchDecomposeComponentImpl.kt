package com.flipperdevices.archive.search.api

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.flipperdevices.archive.api.SearchDecomposeComponent
import com.flipperdevices.archive.api.SelectKeyPathListener
import com.flipperdevices.archive.search.model.SearchNavigationConfig
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.popOr
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.gulya.anvil.assisted.ContributesAssistedFactory

@ContributesAssistedFactory(AppGraph::class, SearchDecomposeComponent.Factory::class)
class SearchDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted onItemSelected: SelectKeyPathListener?,
    @Assisted private val onBack: DecomposeOnBackParameter,
    private val searchScreenFactory: SearchScreenDecomposeComponentImpl.Factory
) : SearchDecomposeComponent<SearchNavigationConfig>(), ComponentContext by componentContext {

    override val stack: Value<ChildStack<SearchNavigationConfig, DecomposeComponent>> = childStack(
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
        is SearchNavigationConfig.Search -> searchScreenFactory(
            componentContext = componentContext,
            onItemSelected = config.onItemSelected,
            onBack = { navigation.popOr(onBack::invoke) }
        )
    }
}
