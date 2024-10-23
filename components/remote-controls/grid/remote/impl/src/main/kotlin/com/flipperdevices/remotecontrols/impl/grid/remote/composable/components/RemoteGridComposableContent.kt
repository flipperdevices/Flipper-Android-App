package com.flipperdevices.remotecontrols.impl.grid.remote.composable.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.faphub.errors.api.FapErrorSize
import com.flipperdevices.faphub.errors.api.FapHubComposableErrorsRenderer
import com.flipperdevices.ifrmvp.core.ui.button.core.ButtonClickEvent
import com.flipperdevices.ifrmvp.core.ui.layout.shared.GridPagesContent
import com.flipperdevices.remotecontrols.api.FlipperDispatchDialogApi
import com.flipperdevices.remotecontrols.impl.grid.remote.composable.util.contentKey
import com.flipperdevices.remotecontrols.impl.grid.remote.presentation.decompose.RemoteGridComponent

@Composable
internal fun RemoteGridComposableContent(
    remoteGridComponent: RemoteGridComponent,
    flipperDispatchDialogApi: FlipperDispatchDialogApi,
    errorsRenderer: FapHubComposableErrorsRenderer,
    model: RemoteGridComponent.Model,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = model,
        modifier = modifier,
        transitionSpec = { fadeIn().togetherWith(fadeOut()) },
        contentKey = { model.contentKey }
    ) { animatedModel ->
        when (animatedModel) {
            is RemoteGridComponent.Model.Error -> {
                errorsRenderer.ComposableThrowableError(
                    throwable = animatedModel.throwable,
                    onRetry = remoteGridComponent::tryLoad,
                    fapErrorSize = FapErrorSize.FULLSCREEN,
                    modifier = Modifier.fillMaxSize()
                )
            }

            is RemoteGridComponent.Model.Loaded -> {
                flipperDispatchDialogApi.Render(
                    dialogType = animatedModel.flipperDialog,
                    onDismiss = remoteGridComponent::dismissDialog,
                )
                GridPagesContent(
                    pagesLayout = animatedModel.pagesLayout,
                    onButtonClick = { _, clickType, keyIdentifier ->
                        when (clickType) {
                            ButtonClickEvent.SINGLE_CLICK -> {
                                remoteGridComponent.onButtonClick(keyIdentifier)
                            }

                            ButtonClickEvent.HOLD -> {
                                remoteGridComponent.onButtonLongClick(keyIdentifier)
                            }

                            ButtonClickEvent.RELEASE -> {
                                remoteGridComponent.onButtonRelease()
                            }
                        }
                    },
                    onReload = remoteGridComponent::tryLoad,
                    emulatedKeyIdentifier = animatedModel.emulatedKey,
                    isSyncing = animatedModel.isSavingFiles,
                    isConnected = animatedModel.isConnected
                )
            }

            is RemoteGridComponent.Model.Loading -> {
                RemoteGridComposableLoadingContent()
            }
        }
    }
}
