package com.flipperdevices.keyscreen.impl.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.bridge.dao.api.model.FlipperFileType
import com.flipperdevices.bridge.synchronization.api.SynchronizationUiApi
import com.flipperdevices.core.ui.ktx.LocalRouter
import com.flipperdevices.keyscreen.impl.R
import com.flipperdevices.keyscreen.impl.composable.actions.ComposableDelete
import com.flipperdevices.keyscreen.impl.composable.actions.ComposableEmulate
import com.flipperdevices.keyscreen.impl.composable.actions.ComposableNfcEdit
import com.flipperdevices.keyscreen.impl.composable.actions.ComposableRestore
import com.flipperdevices.keyscreen.impl.composable.actions.ComposableSend
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
fun ComposableKeyParsed(
    viewModel: KeyScreenViewModel,
    keyScreenState: KeyScreenState.Ready,
    nfcEditorApi: NfcEditorApi,
    synchronizationUiApi: SynchronizationUiApi,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()
    val router = LocalRouter.current
    Column(modifier = Modifier.verticalScroll(scrollState)) {
        ComposableKeyScreenBar(keyScreenState.flipperKey.path.nameWithoutExtension, onBack)
        ComposableKeyCard(
            keyScreenState.parsedKey,
            keyScreenState.deleteState,
            synchronizationState = if (keyScreenState.deleteState == DeleteState.NOT_DELETED) { ->
                synchronizationUiApi.RenderSynchronizationState(
                    keyScreenState.flipperKey.path,
                    withText = true
                )
            } else null,
            keyScreenState.favoriteState,
            viewModel::setFavorite,
            viewModel::onOpenEdit
        )

        val fileType = keyScreenState.parsedKey.fileType

        if (fileType != null) {
            val emulateModifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, bottom = 24.dp)
            when (fileType) {
                FlipperFileType.SUB_GHZ -> ComposableSend(
                    modifier = emulateModifier,
                    flipperKey = keyScreenState.flipperKey
                )
                FlipperFileType.I_BUTTON,
                FlipperFileType.RFID,
                FlipperFileType.NFC -> ComposableEmulate(
                    modifier = emulateModifier,
                    flipperKey = keyScreenState.flipperKey
                )
                FlipperFileType.INFRARED -> {}
            }
        }

        if (keyScreenState.deleteState == DeleteState.NOT_DELETED) {
            if (nfcEditorApi.isSupportedByNfcEditor(keyScreenState.parsedKey)) {
                ComposableNfcEdit {
                    router.navigateTo(nfcEditorApi.getNfcEditorScreen(keyScreenState.flipperKey))
                }
            }
            ComposableShare(keyScreenState.shareState, viewModel::onShare)
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
