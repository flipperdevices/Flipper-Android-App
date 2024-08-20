package com.flipperdevices.remotecontrols.impl.grid.local.composable.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.core.ui.dialog.composable.busy.ComposableFlipperBusy
import com.flipperdevices.ifrmvp.core.ui.layout.shared.GridPagesContent
import com.flipperdevices.ifrmvp.core.ui.layout.shared.LoadingComposable
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
            // We leave screen on error
            LocalGridComponent.Model.Error -> Unit

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
