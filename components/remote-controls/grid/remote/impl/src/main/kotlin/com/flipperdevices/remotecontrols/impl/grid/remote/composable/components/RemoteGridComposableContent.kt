package com.flipperdevices.remotecontrols.impl.grid.remote.composable.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.dialog.composable.busy.ComposableFlipperBusy
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.ifrmvp.core.ui.layout.shared.ErrorComposable
import com.flipperdevices.ifrmvp.core.ui.layout.shared.GridPagesContent
import com.flipperdevices.remotecontrols.grid.remote.impl.R
import com.flipperdevices.remotecontrols.impl.grid.remote.composable.util.contentKey
import com.flipperdevices.remotecontrols.impl.grid.remote.presentation.decompose.RemoteGridComponent
import com.flipperdevices.rootscreen.api.LocalRootNavigation
import com.flipperdevices.rootscreen.model.RootScreenConfig

@Composable
internal fun RemoteGridComposableContent(
    remoteGridComponent: RemoteGridComponent,
    model: RemoteGridComponent.Model,
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
            RemoteGridComponent.Model.Error -> {
                ErrorComposable(
                    desc = stringResource(R.string.empty_page),
                    onReload = remoteGridComponent::tryLoad
                )
            }

            is RemoteGridComponent.Model.Loaded -> {
                if (animatedModel.isFlipperBusy) {
                    ComposableFlipperBusy(
                        onDismiss = remoteGridComponent::dismissBusyDialog,
                        goToRemote = {
                            remoteGridComponent.dismissBusyDialog()
                            rootNavigation.push(RootScreenConfig.ScreenStreaming)
                        }
                    )
                }
                GridPagesContent(
                    pagesLayout = animatedModel.pagesLayout,
                    onButtonClick = { _, keyIdentifier ->
                        remoteGridComponent.onButtonClick(keyIdentifier)
                    },
                    onReload = remoteGridComponent::tryLoad,
                    emulatedKeyIdentifier = animatedModel.emulatedKey,
                    isSyncing = animatedModel.isSavingFiles,
                    isConnected = animatedModel.isConnected
                )
            }

            is RemoteGridComponent.Model.Loading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = LocalPallet.current.accentSecond,
                        strokeWidth = 2.dp
                    )
                    Text(
                        text = stringResource(R.string.loading_remote),
                        style = LocalTypography.current.bodySB14,
                        color = LocalPalletV2.current.text.body.whiteOnColor
                    )
                }
            }
        }
    }
}
