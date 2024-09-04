package com.flipperdevices.remotecontrols.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent

abstract class BrandsScreenDecomposeComponent(
    componentContext: ComponentContext
) : ScreenDecomposeComponent(componentContext) {

    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            categoryId: Long,
            onBackClick: () -> Unit,
            onBrandClick: (brandId: Long, brandName: String) -> Unit,
            onBrandLongClick: (brandId: Long) -> Unit
        ): BrandsScreenDecomposeComponent
    }
}
