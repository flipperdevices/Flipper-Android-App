package com.flipperdevices.archive.category.api

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.flipperdevices.archive.api.CategoryDecomposeComponent
import com.flipperdevices.archive.category.model.CategoryNavigationConfig
import com.flipperdevices.archive.model.CategoryType
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.popOr
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.gulya.anvil.assisted.ContributesAssistedFactory

@ContributesAssistedFactory(AppGraph::class, CategoryDecomposeComponent.Factory::class)
class CategoryDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted categoryType: CategoryType,
    @Assisted private val onBack: DecomposeOnBackParameter,
    private val categoryFactory: CategoryScreenDecomposeComponentImpl.Factory
) : CategoryDecomposeComponent<CategoryNavigationConfig>(), ComponentContext by componentContext {
    override val stack: Value<ChildStack<CategoryNavigationConfig, DecomposeComponent>> =
        childStack(
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
            navigation = navigation,
            onBack = { navigation.popOr(onBack::invoke) }
        )
    }
}
