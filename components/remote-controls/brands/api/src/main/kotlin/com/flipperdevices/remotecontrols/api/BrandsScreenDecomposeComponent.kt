package com.flipperdevices.remotecontrols.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent

abstract class BrandsScreenDecomposeComponent(
    componentContext: ComponentContext
) : ScreenDecomposeComponent(componentContext) {

    fun interface Factory {
        fun createBrandsComponent(
            componentContext: ComponentContext,
            categoryId: Long,
            onBackClicked: () -> Unit,
            onBrandClicked: (brandId: Long) -> Unit
        ): BrandsScreenDecomposeComponent
    }
}
