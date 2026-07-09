package com.flipperdevices.remotecontrols.impl.brands.presentation.decompose.internal

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.faphub.errors.api.FapHubComposableErrorsRenderer
import com.flipperdevices.remotecontrols.api.BrandsScreenDecomposeComponent
import com.flipperdevices.remotecontrols.impl.brands.composable.BrandsScreen
import com.flipperdevices.remotecontrols.impl.brands.presentation.decompose.BrandsDecomposeComponent
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import uk.kulikov.metro.assisted.ContributesAssistedFactory

@Suppress("LongParameterList")
@ContributesAssistedFactory(AppGraph::class, BrandsScreenDecomposeComponent.Factory::class)
class BrandsScreenDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted categoryId: Long,
    @Assisted onBackClick: () -> Unit,
    @Assisted onBrandClick: (Long, String) -> Unit,
    @Assisted onBrandLongClick: (Long) -> Unit,
    brandsDecomposeComponentFactory: BrandsDecomposeComponent.Factory,
    private val errorsRenderer: FapHubComposableErrorsRenderer
) : BrandsScreenDecomposeComponent(componentContext) {
    private val brandsComponent = brandsDecomposeComponentFactory.createBrandsComponent(
        componentContext = childContext("BrandsComponent"),
        categoryId = categoryId,
        onBackClick = onBackClick,
        onBrandClick = onBrandClick,
        onBrandLongClick = onBrandLongClick
    )

    @Composable
    override fun Render() {
        BrandsScreen(
            brandsDecomposeComponent = brandsComponent,
            errorsRenderer = errorsRenderer
        )
    }
}
