package com.flipperdevices.remotecontrols.impl.brands.composable

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.flipperdevices.faphub.errors.api.FapErrorSize
import com.flipperdevices.faphub.errors.api.FapHubComposableErrorsRenderer
import com.flipperdevices.remotecontrols.impl.brands.composable.composable.BrandsLoadedContent
import com.flipperdevices.remotecontrols.impl.brands.composable.composable.BrandsLoadingComposable
import com.flipperdevices.remotecontrols.impl.brands.composable.composable.ComposableBrandsAppBar
import com.flipperdevices.remotecontrols.impl.brands.presentation.decompose.BrandsDecomposeComponent
import kotlinx.coroutines.Dispatchers

@Composable
fun BrandsScreen(
    brandsDecomposeComponent: BrandsDecomposeComponent,
    errorsRenderer: FapHubComposableErrorsRenderer,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val model by remember(brandsDecomposeComponent, coroutineScope) {
        brandsDecomposeComponent.model(coroutineScope)
    }.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            val query by brandsDecomposeComponent.query.collectAsState(Dispatchers.Main.immediate)
            ComposableBrandsAppBar(
                query = query,
                onQueryChange = brandsDecomposeComponent::onQueryChanged,
                onBackClick = brandsDecomposeComponent::onBackClick
            )
        }
    ) { scaffoldPaddings ->
        Crossfade(targetState = model) { model ->
            when (model) {
                is BrandsDecomposeComponent.Model.Error -> {
                    errorsRenderer.ComposableThrowableError(
                        throwable = model.throwable,
                        onRetry = brandsDecomposeComponent::tryLoad,
                        fapErrorSize = FapErrorSize.FULLSCREEN,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                is BrandsDecomposeComponent.Model.Loaded -> {
                    BrandsLoadedContent(
                        model = model,
                        modifier = Modifier.padding(scaffoldPaddings),
                        onBrandClick = brandsDecomposeComponent::onBrandClick,
                        onBrandLongClick = brandsDecomposeComponent::onBrandLongClick
                    )
                }

                BrandsDecomposeComponent.Model.Loading -> {
                    BrandsLoadingComposable()
                }
            }
        }
    }
}
