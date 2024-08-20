package com.flipperdevices.remotecontrols.impl.brands.composable

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.flipperdevices.ifrmvp.core.ui.layout.shared.ErrorComposable
import com.flipperdevices.ifrmvp.core.ui.layout.shared.SharedTopBar
import com.flipperdevices.remotecontrols.impl.brands.composable.composable.BrandsLoadedContent
import com.flipperdevices.remotecontrols.impl.brands.composable.composable.BrandsLoadingComposable
import com.flipperdevices.remotecontrols.impl.brands.presentation.decompose.BrandsDecomposeComponent
import com.flipperdevices.remotecontrols.brands.impl.R as BrandsR

@Composable
fun BrandsScreen(
    brandsDecomposeComponent: BrandsDecomposeComponent,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val model by remember(brandsDecomposeComponent, coroutineScope) {
        brandsDecomposeComponent.model(coroutineScope)
    }.collectAsState()
    Scaffold(
        modifier = modifier,
        topBar = {
            SharedTopBar(
                title = stringResource(BrandsR.string.brands_title),
                subtitle = stringResource(BrandsR.string.brands_subtitle),
                onBackClick = brandsDecomposeComponent::onBackClick
            )
        }
    ) { scaffoldPaddings ->
        Crossfade(targetState = model) { model ->
            when (model) {
                BrandsDecomposeComponent.Model.Error -> {
                    ErrorComposable(onReload = brandsDecomposeComponent::tryLoad)
                }

                is BrandsDecomposeComponent.Model.Loaded -> {
                    BrandsLoadedContent(
                        model = model,
                        modifier = Modifier.padding(scaffoldPaddings),
                        onBrandClick = brandsDecomposeComponent::onBrandClick
                    )
                }

                BrandsDecomposeComponent.Model.Loading -> {
                    BrandsLoadingComposable()
                }
            }
        }
    }
}
