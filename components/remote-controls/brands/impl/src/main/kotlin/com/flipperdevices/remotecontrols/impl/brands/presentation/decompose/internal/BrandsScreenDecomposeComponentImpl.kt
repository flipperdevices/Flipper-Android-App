package com.flipperdevices.remotecontrols.impl.brands.presentation.decompose.internal

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.remotecontrols.api.BrandsScreenDecomposeComponent
import com.flipperdevices.remotecontrols.impl.brands.composable.BrandsScreen
import com.flipperdevices.remotecontrols.impl.brands.presentation.decompose.BrandsDecomposeComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.gulya.anvil.assisted.ContributesAssistedFactory

@ContributesAssistedFactory(AppGraph::class, BrandsScreenDecomposeComponent.Factory::class)
class BrandsScreenDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted categoryId: Long,
    @Assisted onBackClicked: () -> Unit,
    @Assisted onBrandClicked: (brandId: Long) -> Unit,
    brandsDecomposeComponentFactory: BrandsDecomposeComponent.Factory,
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
