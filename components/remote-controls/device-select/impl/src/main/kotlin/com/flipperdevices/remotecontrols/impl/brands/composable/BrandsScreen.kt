package com.flipperdevices.remotecontrols.impl.brands.composable

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.flipperdevices.remotecontrols.impl.brands.composable.composable.BrandsLoadedContent
import com.flipperdevices.remotecontrols.impl.brands.presentation.decompose.BrandsDecomposeComponent
import com.flipperdevices.remotecontrols.impl.categories.composable.components.ErrorComposable
import com.flipperdevices.remotecontrols.impl.categories.composable.components.LoadingComposable
import com.flipperdevices.remotecontrols.impl.categories.composable.components.SharedTopBar
import com.flipperdevices.remotecontrols.device.select.impl.R as RemoteSetupR

@Composable
internal fun BrandsScreen(brandsDecomposeComponent: BrandsDecomposeComponent) {
    val model by brandsDecomposeComponent.model(rememberCoroutineScope()).collectAsState()
    Scaffold(
        topBar = {
            SharedTopBar(
                title = stringResource(RemoteSetupR.string.brands_title),
                subtitle = stringResource(RemoteSetupR.string.brands_subtitle),
                onBackClicked = brandsDecomposeComponent::onBackClicked
            )
        }
    ) { scaffoldPaddings ->
        Crossfade(targetState = model) { model ->
            when (model) {
                BrandsDecomposeComponent.Model.Error -> {
                    ErrorComposable {
                        brandsDecomposeComponent.tryLoad()
                    }
                }

                is BrandsDecomposeComponent.Model.Loaded -> {
                    BrandsLoadedContent(
                        model = model,
                        modifier = Modifier.padding(scaffoldPaddings),
                        onBrandClicked = brandsDecomposeComponent::onBrandClicked
                    )
                }

                BrandsDecomposeComponent.Model.Loading -> {
                    LoadingComposable()
                }
            }
        }
    }
}
