package com.flipperdevices.widget.screen.api

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.flipperdevices.archive.api.SearchDecomposeComponent
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.popOr
import com.flipperdevices.widget.api.WidgetDecomposeComponent
import com.flipperdevices.widget.screen.model.WidgetNavigationConfig
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.gulya.anvil.assisted.ContributesAssistedFactory

@ContributesAssistedFactory(AppGraph::class, WidgetDecomposeComponent.Factory::class)
class WidgetDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted widgetId: Int,
    @Assisted private val onBack: DecomposeOnBackParameter,
    private val searchScreenFactory: SearchDecomposeComponent.Factory,
    private val widgetOptionsFactory: WidgetOptionsDecomposeComponentImpl.Factory
) : WidgetDecomposeComponent<WidgetNavigationConfig>(), ComponentContext by componentContext {

    override val stack: Value<ChildStack<WidgetNavigationConfig, DecomposeComponent>> = childStack(
        source = navigation,
        serializer = WidgetNavigationConfig.serializer(),
        initialConfiguration = WidgetNavigationConfig.WidgetOptions(widgetId),
        handleBackButton = true,
        childFactory = ::child,
    )

    private fun child(
        config: WidgetNavigationConfig,
        componentContext: ComponentContext
    ): DecomposeComponent = when (config) {
        is WidgetNavigationConfig.SearchScreen -> searchScreenFactory(
            componentContext = componentContext,
            onItemSelected = config.listener,
            onBack = { navigation.popOr(onBack::invoke) }
        )

        is WidgetNavigationConfig.WidgetOptions -> widgetOptionsFactory(
            componentContext = componentContext,
            widgetId = config.widgetId,
            navigation = navigation
        )
    }
}
