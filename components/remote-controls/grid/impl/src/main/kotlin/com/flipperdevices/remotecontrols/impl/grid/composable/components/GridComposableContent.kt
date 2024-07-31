package com.flipperdevices.remotecontrols.impl.grid.composable.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.flipperdevices.core.ui.dialog.composable.busy.ComposableFlipperBusy
import com.flipperdevices.ifrmvp.core.ui.layout.shared.ErrorComposable
import com.flipperdevices.ifrmvp.core.ui.layout.shared.LoadingComposable
import com.flipperdevices.remotecontrols.grid.impl.R
import com.flipperdevices.remotecontrols.impl.grid.composable.util.contentKey
import com.flipperdevices.remotecontrols.impl.grid.presentation.decompose.GridComponent
import com.flipperdevices.rootscreen.api.LocalRootNavigation
import com.flipperdevices.rootscreen.model.RootScreenConfig

@Composable
internal fun GridComposableContent(
    gridComponent: GridComponent,
    model: GridComponent.Model,
    modifier: Modifier = Modifier
) {
    val rootNavigation = LocalRootNavigation.current
    AnimatedContent(
        targetState = model,
        modifier = modifier,
        transitionSpec = { fadeIn().togetherWith(fadeOut()) },
        contentKey = { it.contentKey }
    ) { model ->
        when (model) {
            GridComponent.Model.Error -> {
                ErrorComposable(
                    desc = stringResource(R.string.empty_page),
                    onReload = gridComponent::tryLoad
                )
            }

            is GridComponent.Model.Loaded -> {
                if (model.isFlipperBusy) {
                    ComposableFlipperBusy(
                        onDismiss = gridComponent::dismissBusyDialog,
                        goToRemote = {
                            gridComponent.dismissBusyDialog()
                            rootNavigation.push(RootScreenConfig.ScreenStreaming)
                        }
                    )
                }
                GridComposableLoadedContent(
                    pagesLayout = model.pagesLayout,
                    onButtonClick = { _, keyIdentifier ->
                        gridComponent.onButtonClick(keyIdentifier)
                    },
                    onReload = gridComponent::tryLoad,
                    emulatedKeyIdentifier = model.emulatedKey
                )
            }

            is GridComponent.Model.Loading -> {
                LoadingComposable(progress = model.progress)
            }
        }
    }
}
