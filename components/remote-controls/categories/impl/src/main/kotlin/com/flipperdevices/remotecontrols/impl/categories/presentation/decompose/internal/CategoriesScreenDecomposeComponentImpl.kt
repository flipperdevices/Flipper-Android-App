package com.flipperdevices.remotecontrols.impl.categories.presentation.decompose.internal

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.remotecontrols.api.CategoriesScreenDecomposeComponent
import com.flipperdevices.remotecontrols.impl.categories.composable.DeviceCategoriesScreen
import com.flipperdevices.remotecontrols.impl.categories.presentation.decompose.DeviceCategoriesComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.gulya.anvil.assisted.ContributesAssistedFactory

@ContributesAssistedFactory(AppGraph::class, CategoriesScreenDecomposeComponent.Factory::class)
class CategoriesScreenDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted onBackClick: () -> Unit,
    @Assisted onCategoryClick: (categoryId: Long, categoryName: String) -> Unit,
    deviceCategoriesComponentFactory: DeviceCategoriesComponent.Factory,
) : CategoriesScreenDecomposeComponent(componentContext) {
    private val deviceCategoriesComponent = deviceCategoriesComponentFactory.invoke(
        componentContext = childContext("DeviceCategoriesComponent"),
        onBackClick = onBackClick,
        onCategoryClick = onCategoryClick
    )

    @Composable
    override fun Render() {
        DeviceCategoriesScreen(deviceCategoriesComponent = deviceCategoriesComponent)
    }
}
