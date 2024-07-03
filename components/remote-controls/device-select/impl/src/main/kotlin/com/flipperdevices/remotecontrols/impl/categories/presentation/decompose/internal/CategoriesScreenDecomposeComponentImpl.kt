package com.flipperdevices.remotecontrols.impl.categories.presentation.decompose.internal

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.flipperdevices.remotecontrols.api.CategoriesScreenDecomposeComponent
import com.flipperdevices.remotecontrols.impl.categories.presentation.decompose.DeviceCategoriesComponent
import com.flipperdevices.remotecontrols.impl.categories.composable.DeviceCategoriesScreen

class CategoriesScreenDecomposeComponentImpl(
    componentContext: ComponentContext,
    deviceCategoriesComponentFactory: DeviceCategoriesComponent.Factory,
    onBackClicked: () -> Unit,
    onCategoryClicked: (categoryId: Long) -> Unit
) : CategoriesScreenDecomposeComponent(componentContext) {
    private val deviceCategoriesComponent = deviceCategoriesComponentFactory.create(
        componentContext = childContext("DeviceCategoriesComponent"),
        onBackClicked = onBackClicked,
        onCategoryClicked = onCategoryClicked
    )

    @Composable
    override fun Render() {
        DeviceCategoriesScreen(deviceCategoriesComponent = deviceCategoriesComponent)
    }
}
