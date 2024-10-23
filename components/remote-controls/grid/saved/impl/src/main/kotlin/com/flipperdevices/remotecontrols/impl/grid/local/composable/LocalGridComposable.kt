package com.flipperdevices.remotecontrols.impl.grid.local.composable

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.ifrmvp.core.ui.layout.shared.SharedTopBar
import com.flipperdevices.infrared.api.InfraredConnectionApi.InfraredEmulateState
import com.flipperdevices.remotecontrols.api.FlipperDispatchDialogApi
import com.flipperdevices.remotecontrols.grid.saved.impl.R
import com.flipperdevices.remotecontrols.impl.grid.local.api.LocalGridScreenDecomposeComponent
import com.flipperdevices.remotecontrols.impl.grid.local.composable.components.ComposableInfraredDropDown
import com.flipperdevices.remotecontrols.impl.grid.local.composable.components.ComposableNotification
import com.flipperdevices.remotecontrols.impl.grid.local.composable.components.LocalGridComposableContent
import com.flipperdevices.remotecontrols.impl.grid.local.presentation.decompose.LocalGridComponent

@Composable
@Suppress("LongMethod")
fun LocalGridComposable(
    localGridComponent: LocalGridComponent,
    flipperDispatchDialogApi: FlipperDispatchDialogApi,
    onCallback: (LocalGridScreenDecomposeComponent.Callback) -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    val model by remember(localGridComponent, coroutineScope) {
        localGridComponent.model(coroutineScope)
    }.collectAsState()
    LaunchedEffect(model) {
        if (model is LocalGridComponent.Model.Error) {
            onCallback.invoke(LocalGridScreenDecomposeComponent.Callback.UiFileNotFound)
        }
    }
    Scaffold(
        modifier = modifier,
        topBar = {
            (model as? LocalGridComponent.Model.Loaded)?.let { loadedModel ->
                SharedTopBar(
                    onBackClick = localGridComponent::pop,
                    background = LocalPalletV2.current.surface.navBar.body.main,
                    backIconTint = LocalPalletV2.current.icon.blackAndWhite.default,
                    title = {
                        Text(
                            text = loadedModel.keyPath.path.nameWithoutExtension,
                            color = LocalPalletV2.current.text.title.primary,
                            style = LocalTypography.current.titleEB18,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    subtitle = {
                        Crossfade((model as? LocalGridComponent.Model.Loaded)?.connectionState) { connectionState ->
                            when (connectionState) {
                                InfraredEmulateState.ALL_GOOD -> {
                                    Text(
                                        text = stringResource(R.string.remote_subtitle),
                                        color = LocalPalletV2.current.text.title.primary,
                                        style = LocalTypography.current.subtitleM12,
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )
                                }

                                InfraredEmulateState.NOT_CONNECTED,
                                InfraredEmulateState.CONNECTING,
                                InfraredEmulateState.SYNCING,
                                InfraredEmulateState.UPDATE_FLIPPER -> {
                                    ComposableNotification(
                                        state = connectionState,
                                        modifier = Modifier
                                    )
                                }

                                null -> Unit
                            }
                        }
                    }
                ) {
                    ComposableInfraredDropDown(
                        onRename = {
                            localGridComponent.onRename {
                                onCallback.invoke(
                                    LocalGridScreenDecomposeComponent.Callback.Rename(loadedModel.keyPath)
                                )
                            }
                        },
                        onDelete = {
                            localGridComponent.onDelete {
                                onCallback.invoke(LocalGridScreenDecomposeComponent.Callback.Deleted)
                            }
                        },
                        onFavorite = localGridComponent::toggleFavorite,
                        isEmulating = loadedModel.emulatedKey != null,
                        isConnected = loadedModel.isConnected,
                        isFavorite = loadedModel.isFavorite,
                    )
                }
            }
        },
        backgroundColor = LocalPalletV2.current.surface.backgroundMain.body,
        scaffoldState = scaffoldState,
        content = { scaffoldPaddings ->
            LocalGridComposableContent(
                localGridComponent = localGridComponent,
                flipperDispatchDialogApi = flipperDispatchDialogApi,
                model = model,
                modifier = Modifier
                    .padding(scaffoldPaddings)
                    .navigationBarsPadding()
            )
        }
    )
}
