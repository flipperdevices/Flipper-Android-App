package com.flipperdevices.remotecontrols.impl.brands.composable.composable

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.ifrmvp.backend.model.BrandModel
import com.flipperdevices.remotecontrols.impl.brands.composable.composable.alphabet.AlphabetSearchComposable
import com.flipperdevices.remotecontrols.impl.brands.presentation.decompose.BrandsDecomposeComponent
import com.flipperdevices.remotecontrols.impl.brands.presentation.util.charSection

@Composable
fun BrandsLoadedContent(
    model: BrandsDecomposeComponent.Model.Loaded,
    onBrandClick: (BrandModel) -> Unit,
    onBrandLongClick: (BrandModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    AlphabetSearchComposable(
        modifier = modifier,
        items = model.sortedBrands,
        toHeader = { it.name.first().uppercaseChar() },
        headers = model.headers,
        listState = listState,
        content = {
            ItemsList(
                modifier = Modifier.weight(1f),
                listState = listState,
                items = model.sortedBrands,
                onClick = onBrandClick,
                onLongClick = onBrandLongClick,
                toString = { it.name },
                toCharSection = { it.charSection() }
            )
        }
    )
}
