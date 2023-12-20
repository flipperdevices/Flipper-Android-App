package com.flipperdevices.widget.screen.api

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
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.flipperdevices.widget.api.WidgetDecomposeComponent
import com.flipperdevices.widget.screen.model.WidgetNavigationConfig
import com.squareup.anvil.annotations.ContributesBinding
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class WidgetDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted widgetId: Int,
    private val searchScreenFactory: SearchDecomposeComponent.Factory,
    private val widgetOptionsFactory: WidgetOptionsDecomposeComponentImpl.Factory
) : WidgetDecomposeComponent, ComponentContext by componentContext {
    private val navigation = StackNavigation<WidgetNavigationConfig>()

    private val stack: Value<ChildStack<*, DecomposeComponent>> = childStack(
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
            onItemSelected = config.listener
        )

        is WidgetNavigationConfig.WidgetOptions -> widgetOptionsFactory(
            componentContext = componentContext,
            widgetId = config.widgetId,
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
    @ContributesBinding(AppGraph::class, WidgetDecomposeComponent.Factory::class)
    interface Factory : WidgetDecomposeComponent.Factory {
        override fun invoke(
            componentContext: ComponentContext,
            widgetId: Int
        ): WidgetDecomposeComponentImpl
    }
}
