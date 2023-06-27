package com.flipperdevices.keyscreen.impl.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.synchronization.api.SynchronizationUiApi
import com.flipperdevices.keyemulate.api.KeyEmulateApi
import com.flipperdevices.keyscreen.impl.R
import com.flipperdevices.keyscreen.impl.composable.actions.ComposableDelete
import com.flipperdevices.keyscreen.impl.composable.actions.ComposableNfcEdit
import com.flipperdevices.keyscreen.impl.composable.actions.ComposableRestore
import com.flipperdevices.keyscreen.impl.composable.actions.ComposableShare
import com.flipperdevices.keyscreen.impl.composable.card.ComposableKeyCard
import com.flipperdevices.keyscreen.impl.viewmodel.KeyScreenViewModel
import com.flipperdevices.keyscreen.model.DeleteState
import com.flipperdevices.keyscreen.model.KeyScreenState
import com.flipperdevices.keyscreen.shared.bar.ComposableBarBackIcon
import com.flipperdevices.keyscreen.shared.bar.ComposableBarTitleWithName
import com.flipperdevices.keyscreen.shared.bar.ComposableKeyScreenAppBar
import com.flipperdevices.nfceditor.api.NfcEditorApi

@Composable
@Suppress("LongParameterList")
fun ComposableKeyParsed(
    viewModel: KeyScreenViewModel,
    keyScreenState: KeyScreenState.Ready,
    nfcEditorApi: NfcEditorApi,
    synchronizationUiApi: SynchronizationUiApi,
    keyEmulateApi: KeyEmulateApi,
    onBack: () -> Unit,
    onShare: (FlipperKeyPath) -> Unit,
    onOpenNfcEditor: (FlipperKeyPath) -> Unit,
    onOpenEditScreen: (FlipperKeyPath) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Column(modifier = modifier.verticalScroll(scrollState)) {
        ComposableKeyScreenBar(keyScreenState.flipperKey.path.nameWithoutExtension, onBack = onBack)
        ComposableKeyCard(
            keyScreenState.parsedKey,
            keyScreenState.deleteState,
            Modifier.padding(all = 24.dp),
            synchronizationState = if (keyScreenState.deleteState == DeleteState.NOT_DELETED) {
                { ->
                    synchronizationUiApi.RenderSynchronizationState(
                        keyScreenState.flipperKey.getKeyPath(),
                        withText = true
                    )
                }
            } else {
                null
            },
            keyScreenState.favoriteState,
            viewModel::setFavorite,
            onEditName = {
                viewModel.onOpenEdit(onOpenEditScreen)
            }
        )

        if (keyScreenState.deleteState == DeleteState.NOT_DELETED) {
            val emulateConfig = remember { viewModel.getEmulateConfig() }
            emulateConfig?.let { config ->

                keyEmulateApi.ComposableEmulateButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, bottom = 14.dp),
                    emulateConfig = config,
                    isSynchronized = keyScreenState.flipperKey.synchronized
                )
            }

            if (nfcEditorApi.isSupportedByNfcEditor(keyScreenState.parsedKey)) {
                ComposableNfcEdit {
                    onOpenNfcEditor(keyScreenState.flipperKey.getKeyPath())
                }
            }
            ComposableShare(keyScreenState.shareState) {
                onShare(keyScreenState.flipperKey.getKeyPath())
            }
        } else if (keyScreenState.deleteState == DeleteState.DELETED) {
            ComposableRestore {
                viewModel.onRestore(onBack)
            }
        }

        ComposableDelete(keyScreenState.deleteState) {
            viewModel.onDelete(onBack)
        }
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
