package com.flipperdevices.archive.category.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.flipperdevices.archive.api.CategoryDecomposeComponent
import com.flipperdevices.archive.category.model.CategoryNavigationConfig
import com.flipperdevices.archive.model.CategoryType
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.keyscreen.api.KeyScreenDecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.squareup.anvil.annotations.ContributesBinding
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class CategoryDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted categoryType: CategoryType,
    private val categoryFactory: CategoryScreenDecomposeComponentImpl.Factory
) : CategoryDecomposeComponent, ComponentContext by componentContext {
    private val navigation = StackNavigation<CategoryNavigationConfig>()

    private val stack: Value<ChildStack<*, DecomposeComponent>> = childStack(
        source = navigation,
        serializer = CategoryNavigationConfig.serializer(),
        initialStack = {
            listOf(CategoryNavigationConfig.Category(categoryType))
        },
        handleBackButton = true,
        childFactory = ::child,
    )

    private fun child(
        config: CategoryNavigationConfig,
        componentContext: ComponentContext
    ): DecomposeComponent = when (config) {
        is CategoryNavigationConfig.Category -> categoryFactory(
            componentContext = componentContext,
            categoryType = config.categoryType,
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
    @ContributesBinding(AppGraph::class, CategoryDecomposeComponent.Factory::class)
    interface Factory : CategoryDecomposeComponent.Factory {
        override fun invoke(
            componentContext: ComponentContext,
            categoryType: CategoryType
        ): CategoryDecomposeComponentImpl
    }
}
