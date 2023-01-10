package com.flipperdevices.keyscreen.impl.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.bridge.synchronization.api.SynchronizationUiApi
import com.flipperdevices.core.ui.ktx.LocalRouter
import com.flipperdevices.keyscreen.api.KeyEmulateApi
import com.flipperdevices.keyscreen.impl.R
import com.flipperdevices.keyscreen.impl.composable.actions.ComposableDelete
import com.flipperdevices.keyscreen.impl.composable.actions.ComposableNfcEdit
import com.flipperdevices.keyscreen.impl.composable.actions.ComposableRestore
import com.flipperdevices.keyscreen.impl.composable.actions.ComposableShare
import com.flipperdevices.keyscreen.impl.composable.card.ComposableKeyCard
import com.flipperdevices.keyscreen.impl.model.DeleteState
import com.flipperdevices.keyscreen.impl.model.KeyScreenState
import com.flipperdevices.keyscreen.impl.viewmodel.KeyScreenViewModel
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
    onShare: () -> Unit
) {
    val scrollState = rememberScrollState()
    val router = LocalRouter.current
    Column(modifier = Modifier.verticalScroll(scrollState)) {
        ComposableKeyScreenBar(keyScreenState.flipperKey.path.nameWithoutExtension) {
            router.exit()
        }
        ComposableKeyCard(
            Modifier.padding(all = 24.dp),
            keyScreenState.parsedKey,
            keyScreenState.deleteState,
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
                viewModel.onOpenEdit(router)
            }
        )

        keyEmulateApi.ComposableEmulateButton(
            Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, bottom = 14.dp),
            keyScreenState.flipperKey
        )

        if (keyScreenState.deleteState == DeleteState.NOT_DELETED) {
            if (nfcEditorApi.isSupportedByNfcEditor(keyScreenState.parsedKey)) {
                ComposableNfcEdit {
                    viewModel.onNfcEdit(keyScreenState.flipperKey)
                }
            }
            ComposableShare(keyScreenState.shareState, onShare = onShare)
        } else if (keyScreenState.deleteState == DeleteState.DELETED) {
            ComposableRestore {
                viewModel.onRestore(router)
            }
        }

        ComposableDelete(keyScreenState.deleteState) {
            viewModel.onDelete(router)
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
