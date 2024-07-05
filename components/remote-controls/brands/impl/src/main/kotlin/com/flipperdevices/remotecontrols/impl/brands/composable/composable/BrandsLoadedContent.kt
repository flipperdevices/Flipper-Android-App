package com.flipperdevices.remotecontrols.impl.brands.composable.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.ifrmvp.backend.model.BrandModel
import com.flipperdevices.remotecontrols.impl.brands.presentation.decompose.BrandsDecomposeComponent

@Composable
fun BrandsLoadedContent(
    model: BrandsDecomposeComponent.Model.Loaded,
    onBrandClicked: (BrandModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    AlphabetSearchComposable(
        model = model,
        modifier = modifier,
        onBrandClicked = onBrandClicked
    )
}
