package com.flipperdevices.faphub.category.impl.api

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.faphub.category.api.FapHubCategoryDecomposeComponent
import com.flipperdevices.faphub.category.impl.model.FapCategoryNavigationConfig
import com.flipperdevices.faphub.dao.api.model.FapCategory
import com.flipperdevices.faphub.fapscreen.api.FapScreenDecomposeComponent
import com.flipperdevices.faphub.search.api.FapHubSearchDecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.popOr
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.gulya.anvil.assisted.ContributesAssistedFactory

@ContributesAssistedFactory(AppGraph::class, FapHubCategoryDecomposeComponent.Factory::class)
class FapHubCategoryDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val fapCategory: FapCategory,
    @Assisted private val onBack: DecomposeOnBackParameter,
    private val fapScreenFactory: FapScreenDecomposeComponent.Factory,
    private val fapSearchFactory: FapHubSearchDecomposeComponent.Factory,
    private val fapCategoryScreenFactory: FapHubCategoryScreenDecomposeComponentImpl.Factory
) : FapHubCategoryDecomposeComponent<FapCategoryNavigationConfig>(), ComponentContext by componentContext {
    override val stack: Value<ChildStack<FapCategoryNavigationConfig, DecomposeComponent>> = childStack(
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
            navigation = navigation,
            onBack = { navigation.popOr(onBack::invoke) }
        )

        is FapCategoryNavigationConfig.FapScreen -> fapScreenFactory(
            componentContext = componentContext,
            id = config.id,
            onBack = { navigation.popOr(onBack::invoke) }
        )

        FapCategoryNavigationConfig.Search -> fapSearchFactory(
            componentContext = componentContext,
            onBack = { navigation.popOr(onBack::invoke) }
        )
    }
}
