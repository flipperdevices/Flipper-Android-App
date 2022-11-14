package com.flipperdevices.share.receive.composable

import androidx.compose.runtime.Composable
import com.flipperdevices.keyscreen.api.KeyScreenApi
import com.flipperdevices.share.receive.composable.screen.ComposableKeyScreenProgress
import com.flipperdevices.share.receive.model.ReceiveState
import com.flipperdevices.share.receive.viewmodels.KeyReceiveViewModel

@Composable
fun ComposableKeyReceive(
    keyScreenApi: KeyScreenApi,
    state: ReceiveState,
    viewModel: KeyReceiveViewModel,
    onCancel: () -> Unit = {}
) {
    when (state) {
        ReceiveState.NotStarted -> ComposableKeyScreenProgress()
        is ReceiveState.Pending -> ComposableKeySaveScreen(
            keyScreenApi = keyScreenApi,
            keyParsed = state.parsed,
            savingInProgress = false,
            onSave = viewModel::onSave,
            onCancel = onCancel
        )
        is ReceiveState.Saving -> ComposableKeySaveScreen(
            keyScreenApi = keyScreenApi,
            keyParsed = state.parsed,
            savingInProgress = true,
            onSave = viewModel::onSave,
            onCancel = onCancel
        )
        is ReceiveState.Error -> ComposableKeyErrorScreen(
            typeError = state.type,
            onCancel = onCancel,
            onRetry = viewModel::onRetry
        )
        ReceiveState.Finished -> return
    }
}
