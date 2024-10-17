package com.flipperdevices.remotecontrols.impl.categories.composable

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.faphub.errors.api.FapErrorSize
import com.flipperdevices.faphub.errors.api.FapHubComposableErrorsRenderer
import com.flipperdevices.ifrmvp.core.ui.layout.shared.SharedTopBar
import com.flipperdevices.remotecontrols.impl.categories.composable.components.DeviceCategoriesLoadedContent
import com.flipperdevices.remotecontrols.impl.categories.composable.components.DeviceCategoriesLoadingContent
import com.flipperdevices.remotecontrols.impl.categories.presentation.decompose.DeviceCategoriesComponent
import com.flipperdevices.remotecontrols.categories.impl.R as CategoriesR

@Composable
internal fun DeviceCategoriesScreen(
    deviceCategoriesComponent: DeviceCategoriesComponent,
    errorsRenderer: FapHubComposableErrorsRenderer,
    modifier: Modifier = Modifier
) {
    val model by deviceCategoriesComponent.model.collectAsState()
    Scaffold(
        modifier = modifier,
        topBar = {
            SharedTopBar(
                title = stringResource(CategoriesR.string.categories_title),
                subtitle = stringResource(CategoriesR.string.rcc_step_1),
                onBackClick = deviceCategoriesComponent::onBackClick
            )
        },
        backgroundColor = LocalPalletV2.current.surface.backgroundMain.body
    ) { scaffoldPaddings ->
        Crossfade(model) { model ->
            when (model) {
                is DeviceCategoriesComponent.Model.Error -> {
                    errorsRenderer.ComposableThrowableError(
                        throwable = model.throwable,
                        onRetry = deviceCategoriesComponent::tryLoad,
                        fapErrorSize = FapErrorSize.FULLSCREEN,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                is DeviceCategoriesComponent.Model.Loaded -> {
                    DeviceCategoriesLoadedContent(
                        model = model,
                        modifier = Modifier.padding(scaffoldPaddings),
                        onCategoryClick = deviceCategoriesComponent::onCategoryClick
                    )
                }

                DeviceCategoriesComponent.Model.Loading -> {
                    DeviceCategoriesLoadingContent()
                }
            }
        }
    }
}
