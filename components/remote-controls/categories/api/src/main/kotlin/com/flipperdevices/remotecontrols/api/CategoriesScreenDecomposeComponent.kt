package com.flipperdevices.remotecontrols.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent

abstract class CategoriesScreenDecomposeComponent(
    componentContext: ComponentContext
) : ScreenDecomposeComponent(componentContext) {

    fun interface Factory {
        fun invoke(
            componentContext: ComponentContext,
            onBackClick: () -> Unit,
            onCategoryClick: (categoryId: Long) -> Unit
        ): CategoriesScreenDecomposeComponent
    }
}
