package com.flipperdevices.main.impl.api

import androidx.compose.runtime.getValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.faphub.category.api.FapHubCategoryDecomposeComponent
import com.flipperdevices.faphub.fapscreen.api.FapScreenDecomposeComponent
import com.flipperdevices.faphub.main.api.FapHubDecomposeComponent
import com.flipperdevices.faphub.search.api.FapHubSearchDecomposeComponent
import com.flipperdevices.main.impl.model.FapHubNavigationConfig
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.popOr
import com.squareup.anvil.annotations.ContributesBinding
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

@Suppress("LongParameterList")
class FapHubDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted deeplink: Deeplink.BottomBar.HubTab.FapHub?,
    @Assisted private val onBack: DecomposeOnBackParameter,
    private val fapScreenFactory: FapScreenDecomposeComponent.Factory,
    private val fapSearchFactory: FapHubSearchDecomposeComponent.Factory,
    private val fapCategoryFactory: FapHubCategoryDecomposeComponent.Factory,
    private val mainScreenFactory: MainScreenDecomposeComponentImpl.Factory
) : FapHubDecomposeComponent<FapHubNavigationConfig>(), ComponentContext by componentContext {
    override val stack: Value<ChildStack<FapHubNavigationConfig, DecomposeComponent>> = childStack(
        source = navigation,
        serializer = FapHubNavigationConfig.serializer(),
        initialStack = {
            if (deeplink is Deeplink.BottomBar.HubTab.FapHub.Fap) {
                listOf(
                    FapHubNavigationConfig.Main,
                    FapHubNavigationConfig.FapScreen(deeplink.appId)
                )
            } else {
                listOf(FapHubNavigationConfig.Main)
            }
        },
        handleBackButton = true,
        childFactory = ::child,
    )

    private fun child(
        config: FapHubNavigationConfig,
        componentContext: ComponentContext
    ): DecomposeComponent = when (config) {
        FapHubNavigationConfig.Main -> mainScreenFactory(
            componentContext = componentContext,
            navigation = navigation,
            onBack = { navigation.popOr(onBack::invoke) }
        )

        is FapHubNavigationConfig.FapScreen -> fapScreenFactory(
            componentContext = componentContext,
            id = config.id,
            onBack = { navigation.popOr(onBack::invoke) }
        )

        FapHubNavigationConfig.Search -> fapSearchFactory(
            componentContext = componentContext,
            onBack = { navigation.popOr(onBack::invoke) }
        )

        is FapHubNavigationConfig.Category -> fapCategoryFactory(
            componentContext = componentContext,
            category = config.fapCategory,
            onBack = { navigation.popOr(onBack::invoke) }
        )
    }

    @AssistedFactory
    @ContributesBinding(AppGraph::class, FapHubDecomposeComponent.Factory::class)
    interface Factory : FapHubDecomposeComponent.Factory {
        override fun invoke(
            componentContext: ComponentContext,
            deeplink: Deeplink.BottomBar.HubTab.FapHub?,
            onBack: DecomposeOnBackParameter
        ): FapHubDecomposeComponentImpl
    }
}
