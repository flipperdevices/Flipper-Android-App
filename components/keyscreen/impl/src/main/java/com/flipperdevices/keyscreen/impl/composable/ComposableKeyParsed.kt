package com.flipperdevices.keyscreen.impl.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.synchronization.api.SynchronizationUiApi
import com.flipperdevices.keyemulate.api.KeyEmulateApi
import com.flipperdevices.keyemulate.model.EmulateConfig
import com.flipperdevices.keyscreen.impl.R
import com.flipperdevices.keyscreen.impl.composable.actions.ComposableDelete
import com.flipperdevices.keyscreen.impl.composable.actions.ComposableNfcEdit
import com.flipperdevices.keyscreen.impl.composable.actions.ComposableRestore
import com.flipperdevices.keyscreen.impl.composable.actions.ComposableShare
import com.flipperdevices.keyscreen.impl.composable.card.ComposableKeyCard
import com.flipperdevices.keyscreen.model.DeleteState
import com.flipperdevices.keyscreen.model.KeyScreenState
import com.flipperdevices.keyscreen.shared.bar.ComposableBarBackIcon
import com.flipperdevices.keyscreen.shared.bar.ComposableBarTitleWithName
import com.flipperdevices.keyscreen.shared.bar.ComposableKeyScreenAppBar

@Composable
@Suppress("LongParameterList", "NonSkippableComposable")
fun ComposableKeyParsed(
    keyScreenState: KeyScreenState.Ready,
    synchronizationUiApi: SynchronizationUiApi,
    keyEmulateApi: KeyEmulateApi,
    componentContext: ComponentContext,
    onBack: () -> Unit,
    onShare: () -> Unit,
    onOpenNfcEditor: (FlipperKeyPath) -> Unit,
    onRestore: () -> Unit,
    onDelete: () -> Unit,
    setFavorite: (Boolean) -> Unit,
    onEdit: () -> Unit,
    emulateConfig: EmulateConfig?,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Column(modifier = modifier.verticalScroll(scrollState)) {
        ComposableKeyScreenBar(keyScreenState.flipperKey.path.nameWithoutExtension, onBack = onBack)
        ComposableKeyCard(
            parsedKey = keyScreenState.parsedKey,
            deleteState = keyScreenState.deleteState,
            modifier = Modifier.padding(all = 24.dp),
            synchronizationState = if (keyScreenState.deleteState == DeleteState.NOT_DELETED) {
                { ->
                    synchronizationUiApi.RenderSynchronizationState(
                        keyPath = keyScreenState.flipperKey.getKeyPath(),
                        withText = true,
                        componentContext = componentContext
                    )
                }
            } else {
                null
            },
            favoriteState = keyScreenState.favoriteState,
            onSwitchFavorites = setFavorite,
            onEditName = onEdit,
            emulatingInProgress = keyScreenState.emulatingInProgress
        )

        if (keyScreenState.deleteState == DeleteState.NOT_DELETED) {
            emulateConfig?.let { config ->
                keyEmulateApi.ComposableEmulateButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, bottom = 14.dp),
                    emulateConfig = config,
                    isSynchronized = keyScreenState.flipperKey.synchronized,
                    componentContext = componentContext
                )
            }

            if (keyScreenState.isSupportEditing) {
                ComposableNfcEdit(
                    emulatingInProgress = keyScreenState.emulatingInProgress,
                    onClick = {
                        onOpenNfcEditor(keyScreenState.flipperKey.getKeyPath())
                    }
                )
            }
            ComposableShare(keyScreenState.shareState) {
                onShare()
            }
        } else if (keyScreenState.deleteState == DeleteState.DELETED) {
            ComposableRestore(onClick = onRestore)
        }

        ComposableDelete(keyScreenState.deleteState, onClick = onDelete)
    }
}

@Composable
private fun ComposableKeyScreenBar(keyName: String, onBack: () -> Unit) {
    ComposableKeyScreenAppBar(
        startBlock = {
            ComposableBarBackIcon(it, onBack)
        },
        centerBlock = {
            ComposableBarTitleWithName(
                modifier = it,
                titleId = R.string.keyscreen_title,
                name = keyName
            )
        }
    )
}
