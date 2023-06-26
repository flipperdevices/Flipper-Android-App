package com.flipperdevices.share.receive.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.flipperdevices.keyscreen.api.KeyScreenApi
import com.flipperdevices.share.receive.composable.screens.ComposableKeyErrorScreen
import com.flipperdevices.share.receive.composable.screens.ComposableKeyInProgressScreen
import com.flipperdevices.share.receive.composable.screens.ComposableKeySaveScreen
import com.flipperdevices.share.receive.models.ReceiveState
import com.flipperdevices.share.receive.viewmodels.KeyReceiveViewModel
import tangle.viewmodel.compose.tangleViewModel

@Composable
fun ComposableKeyReceive(
    keyScreenApi: KeyScreenApi,
    onCancel: () -> Unit
) {
    val viewModel: KeyReceiveViewModel = tangleViewModel()
    val state by viewModel.getState().collectAsState()

    when (val localState = state) {
        is ReceiveState.Pending -> ComposableKeySaveScreen(
            keyScreenApi = keyScreenApi,
            keyParsed = localState.parsed,
            savingInProgress = localState.isSaving,
            onSave = viewModel::onSave,
            onCancel = onCancel
        )
        is ReceiveState.Error -> ComposableKeyErrorScreen(
            typeError = localState.type,
            onCancel = onCancel,
            onRetry = viewModel::onRetry
        )
        ReceiveState.NotStarted -> ComposableKeyInProgressScreen(
            keyScreenApi = keyScreenApi,
            onCancel = onCancel
        )
        ReceiveState.Finished -> {
            LaunchedEffect(Unit) {
                viewModel.onFinish()
                onCancel()
            }
        }
    }
}
