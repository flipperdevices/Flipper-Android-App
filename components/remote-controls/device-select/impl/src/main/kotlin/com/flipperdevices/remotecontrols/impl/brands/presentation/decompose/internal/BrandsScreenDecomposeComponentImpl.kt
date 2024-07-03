package com.flipperdevices.remotecontrols.impl.brands.presentation.decompose.internal

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.flipperdevices.remotecontrols.impl.brands.presentation.decompose.BrandsDecomposeComponent
import com.flipperdevices.remotecontrols.api.BrandsScreenDecomposeComponent
import com.flipperdevices.remotecontrols.impl.brands.composable.BrandsScreen

internal class BrandsScreenDecomposeComponentImpl(
    componentContext: ComponentContext,
    brandsDecomposeComponentFactory: BrandsDecomposeComponent.Factory,
    categoryId: Long,
    onBackClicked: () -> Unit,
    onBrandClicked: (brandId: Long) -> Unit
) : BrandsScreenDecomposeComponent(componentContext) {
    private val brandsComponent = brandsDecomposeComponentFactory.createBrandsComponent(
        componentContext = childContext("BrandsComponent"),
        categoryId = categoryId,
        onBackClicked = onBackClicked,
        onBrandClicked = onBrandClicked
    )

    @Composable
    override fun Render() {
        BrandsScreen(brandsDecomposeComponent = brandsComponent)
    }
}
