package com.flipperdevices.remotecontrols.impl.grid.local.composable.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.flipperdevices.core.ui.dialog.composable.busy.ComposableFlipperBusy
import com.flipperdevices.ifrmvp.core.ui.layout.shared.ErrorComposable
import com.flipperdevices.ifrmvp.core.ui.layout.shared.GridPagesContent
import com.flipperdevices.ifrmvp.core.ui.layout.shared.LoadingComposable
import com.flipperdevices.remotecontrols.grid.saved.impl.R
import com.flipperdevices.remotecontrols.impl.grid.local.composable.util.contentKey
import com.flipperdevices.remotecontrols.impl.grid.local.presentation.decompose.LocalGridComponent
import com.flipperdevices.rootscreen.api.LocalRootNavigation
import com.flipperdevices.rootscreen.model.RootScreenConfig

@Composable
internal fun LocalGridComposableContent(
    localGridComponent: LocalGridComponent,
    model: LocalGridComponent.Model,
    modifier: Modifier = Modifier
) {
    val rootNavigation = LocalRootNavigation.current
    AnimatedContent(
        targetState = model,
        modifier = modifier,
        transitionSpec = { fadeIn().togetherWith(fadeOut()) },
        contentKey = { model.contentKey }
    ) { animatedModel ->
        when (animatedModel) {
            LocalGridComponent.Model.Error -> {
                ErrorComposable(
                    desc = stringResource(R.string.empty_page),
                )
            }

            is LocalGridComponent.Model.Loaded -> {
                if (animatedModel.isFlipperBusy) {
                    ComposableFlipperBusy(
                        onDismiss = localGridComponent::dismissBusyDialog,
                        goToRemote = {
                            localGridComponent.dismissBusyDialog()
                            rootNavigation.push(RootScreenConfig.ScreenStreaming)
                        }
                    )
                }
                GridPagesContent(
                    pagesLayout = animatedModel.pagesLayout,
                    onButtonClick = { _, keyIdentifier ->
                        localGridComponent.onButtonClick(keyIdentifier)
                    },
                    emulatedKeyIdentifier = animatedModel.emulatedKey,
                    isSyncing = animatedModel.isSynchronizing
                )
            }

            is LocalGridComponent.Model.Loading -> {
                LoadingComposable()
            }
        }
    }
}
