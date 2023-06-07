package com.flipperdevices.keyscreen.impl.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.synchronization.api.SynchronizationUiApi
import com.flipperdevices.keyemulate.api.KeyEmulateApi
import com.flipperdevices.keyscreen.impl.viewmodel.KeyScreenViewModel
import com.flipperdevices.keyscreen.model.KeyScreenState
import com.flipperdevices.nfceditor.api.NfcEditorApi

@Composable
fun ComposableKeyScreen(
    viewModel: KeyScreenViewModel,
    synchronizationUiApi: SynchronizationUiApi,
    nfcEditorApi: NfcEditorApi,
    keyEmulateApi: KeyEmulateApi,
    onBack: () -> Unit,
    onShare: (FlipperKeyPath) -> Unit,
    onOpenNfcEditor: (FlipperKeyPath) -> Unit,
    onOpenEditScreen: (FlipperKeyPath) -> Unit
) {
    val keyScreenState by viewModel.getKeyScreenState().collectAsState()

    when (val localKeyScreenState = keyScreenState) {
        KeyScreenState.InProgress -> ComposableKeyInitial()
        is KeyScreenState.Error -> ComposableKeyError(localKeyScreenState)
        is KeyScreenState.Ready -> ComposableKeyParsed(
            viewModel,
            localKeyScreenState,
            nfcEditorApi,
            synchronizationUiApi,
            keyEmulateApi,
            onShare = onShare,
            onBack = onBack,
            onOpenNfcEditor = onOpenNfcEditor,
            onOpenEditScreen = onOpenEditScreen
        )
    }
}

@Composable
private fun ComposableKeyInitial() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(modifier = Modifier.size(48.dp))
    }
}

@Composable
private fun ComposableKeyError(error: KeyScreenState.Error) {
    val errorText = stringResource(error.reason)
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = errorText,
            fontWeight = FontWeight.Medium
        )
    }
}
