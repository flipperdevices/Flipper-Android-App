package com.flipperdevices.keyscreen.impl.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.synchronization.api.SynchronizationUiApi
import com.flipperdevices.core.ui.ktx.SetUpNavigationBarColor
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.keyemulate.api.KeyEmulateApi
import com.flipperdevices.keyscreen.impl.viewmodel.KeyScreenViewModel
import com.flipperdevices.keyscreen.model.KeyScreenState
import com.flipperdevices.keyscreen.shared.screen.ComposableKeyScreenError
import com.flipperdevices.keyscreen.shared.screen.ComposableKeyScreenLoading
import com.flipperdevices.nfceditor.api.NfcEditorApi

@Composable
fun ComposableKeyScreen(
    viewModel: KeyScreenViewModel,
    synchronizationUiApi: SynchronizationUiApi,
    nfcEditorApi: NfcEditorApi,
    keyEmulateApi: KeyEmulateApi,
    onBack: () -> Unit,
    onShare: () -> Unit,
    onOpenNfcEditor: (FlipperKeyPath) -> Unit,
    onOpenEditScreen: (FlipperKeyPath) -> Unit
) {
    val keyScreenState by viewModel.getKeyScreenState().collectAsState()

    when (val localKeyScreenState = keyScreenState) {
        KeyScreenState.InProgress -> ComposableKeyScreenLoading()
        is KeyScreenState.Error -> ComposableKeyScreenError(
            text = stringResource(id = localKeyScreenState.reason)
        )

        is KeyScreenState.Ready -> ComposableKeyParsed(
            localKeyScreenState,
            nfcEditorApi,
            synchronizationUiApi,
            keyEmulateApi,
            onShare = onShare,
            onBack = onBack,
            onOpenNfcEditor = onOpenNfcEditor,
            setFavorite = viewModel::setFavorite,
            onEdit = { viewModel.onOpenEdit(onOpenEditScreen) },
            emulateConfig = localKeyScreenState.emulateConfig,
            onRestore = { viewModel.onRestore(onBack) },
            onDelete = { viewModel.onDelete(onBack) }
        )
    }
    SetUpNavigationBarColor(color = LocalPallet.current.background)
}
