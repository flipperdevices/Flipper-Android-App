package com.flipperdevices.remotecontrols.impl.categories.composable

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.ifrmvp.core.ui.layout.shared.ErrorComposable
import com.flipperdevices.ifrmvp.core.ui.layout.shared.LoadingComposable
import com.flipperdevices.ifrmvp.core.ui.layout.shared.SharedTopBar
import com.flipperdevices.remotecontrols.impl.categories.composable.components.DeviceCategoriesLoadedContent
import com.flipperdevices.remotecontrols.impl.categories.presentation.decompose.DeviceCategoriesComponent
import com.flipperdevices.remotecontrols.categories.impl.R as CategoriesR

@Composable
internal fun DeviceCategoriesScreen(deviceCategoriesComponent: DeviceCategoriesComponent) {
    val model by deviceCategoriesComponent.model.collectAsState()
    Scaffold(
        topBar = {
            SharedTopBar(
                title = stringResource(CategoriesR.string.categories_title),
                subtitle = stringResource(CategoriesR.string.categories_subtitle),
                onBackClicked = deviceCategoriesComponent::onBackClicked
            )
        },
        backgroundColor = LocalPalletV2.current.surface.backgroundMain.body
    ) { scaffoldPaddings ->
        Crossfade(model) { model ->
            when (model) {
                DeviceCategoriesComponent.Model.Error -> {
                    ErrorComposable {
                        deviceCategoriesComponent.tryLoad()
                    }
                }

                is DeviceCategoriesComponent.Model.Loaded -> {
                    DeviceCategoriesLoadedContent(
                        model = model,
                        modifier = Modifier.padding(scaffoldPaddings),
                        onCategoryClicked = deviceCategoriesComponent::onCategoryClicked
                    )
                }

                DeviceCategoriesComponent.Model.Loading -> {
                    LoadingComposable()
                }
            }
        }
    }
}
