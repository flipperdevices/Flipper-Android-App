package com.flipperdevices.remotecontrols.impl.grid.local.composable.components

import android.util.Log
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
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.ifrmvp.core.ui.button.core.ButtonClickEvent
import com.flipperdevices.ifrmvp.core.ui.layout.shared.GridPagesContent
import com.flipperdevices.remotecontrols.api.FlipperDispatchDialogApi
import com.flipperdevices.remotecontrols.grid.saved.impl.R
import com.flipperdevices.remotecontrols.impl.grid.local.composable.util.contentKey
import com.flipperdevices.remotecontrols.impl.grid.local.presentation.decompose.LocalGridComponent

@Composable
internal fun LocalGridComposableContent(
    localGridComponent: LocalGridComponent,
    flipperDispatchDialogApi: FlipperDispatchDialogApi,
    model: LocalGridComponent.Model,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = model,
        modifier = modifier,
        transitionSpec = { fadeIn().togetherWith(fadeOut()) },
        contentKey = { it.contentKey }
    ) { animatedModel ->
        when (animatedModel) {
            // We leave screen on error
            LocalGridComponent.Model.Error -> Unit

            is LocalGridComponent.Model.Loaded -> {
                flipperDispatchDialogApi.Render(
                    dialogType = animatedModel.flipperDialog,
                    onDismiss = localGridComponent::dismissDialog
                )
                GridPagesContent(
                    pagesLayout = animatedModel.pagesLayout,
                    onButtonClick = { _, clickType, keyIdentifier ->
                        Log.d("MAKEEVRSERG", "clickType: $clickType")
                        when (clickType) {
                            ButtonClickEvent.SINGLE_CLICK -> {
                                localGridComponent.onButtonClick(keyIdentifier)
                            }

                            ButtonClickEvent.HOLD -> {
                                localGridComponent.onButtonLongClick(keyIdentifier)
                            }

                            ButtonClickEvent.RELEASE -> {
                                localGridComponent.onButtonRelease()
                            }
                        }
                    },
                    emulatedKeyIdentifier = animatedModel.emulatedKey,
                    isSyncing = animatedModel.isSynchronizing,
                    isConnected = animatedModel.isConnected
                )
            }

            is LocalGridComponent.Model.Loading -> {
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
