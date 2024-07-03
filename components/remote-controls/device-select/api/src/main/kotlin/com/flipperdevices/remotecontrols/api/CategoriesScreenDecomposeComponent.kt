package com.flipperdevices.remotecontrols.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent

abstract class CategoriesScreenDecomposeComponent(
    componentContext: ComponentContext
) : ScreenDecomposeComponent(componentContext) {

    fun interface Factory {
        fun create(
            componentContext: ComponentContext,
            onBackClicked: () -> Unit,
            onCategoryClicked: (categoryId: Long) -> Unit
        ): CategoriesScreenDecomposeComponent
    }
}
