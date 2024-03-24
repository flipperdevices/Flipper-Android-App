package com.flipperdevices.faphub.search.impl.api

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.faphub.fapscreen.api.FapScreenDecomposeComponent
import com.flipperdevices.faphub.search.api.FapHubSearchDecomposeComponent
import com.flipperdevices.faphub.search.impl.model.FapHubSearchNavigationConfig
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.popOr
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.gulya.anvil.assisted.ContributesAssistedFactory

@ContributesAssistedFactory(AppGraph::class, FapHubSearchDecomposeComponent.Factory::class)
class FapHubSearchDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val onBack: DecomposeOnBackParameter,
    private val fapScreenFactory: FapScreenDecomposeComponent.Factory,
    private val searchScreenFactory: SearchScreenDecomposeComponentImpl.Factory
) : FapHubSearchDecomposeComponent<FapHubSearchNavigationConfig>(), ComponentContext by componentContext {
    override val stack: Value<ChildStack<FapHubSearchNavigationConfig, DecomposeComponent>> = childStack(
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
            id = config.id,
            onBack = { navigation.popOr(onBack::invoke) }
        )

        FapHubSearchNavigationConfig.SearchScreen -> searchScreenFactory(
            componentContext = componentContext,
            navigation = navigation,
            onBack = { navigation.popOr(onBack::invoke) }
        )
    }
}
