package com.flipperdevices.share.receive.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.flipperdevices.keyscreen.api.KeyScreenApi
import com.flipperdevices.share.receive.composable.screens.ComposableKeyErrorScreen
import com.flipperdevices.share.receive.composable.screens.ComposableKeyInProgressScreen
import com.flipperdevices.share.receive.composable.screens.ComposableKeySaveScreen
import com.flipperdevices.share.receive.models.ReceiveState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ComposableKeyReceive(
    keyScreenApi: KeyScreenApi,
    state: ReceiveState,
    onSave: () -> Unit,
    onRetry: () -> Unit,
    onCancel: () -> Unit
) {
    when (state) {
        is ReceiveState.Pending -> ComposableKeySaveScreen(
            keyScreenApi = keyScreenApi,
            keyParsed = state.parsed,
            savingInProgress = state.isSaving,
            onSave = onSave,
            onCancel = onCancel
        )

        is ReceiveState.Error -> ComposableKeyErrorScreen(
            typeError = state.type,
            onCancel = onCancel,
            onRetry = onRetry
        )

        ReceiveState.NotStarted -> ComposableKeyInProgressScreen(
            keyScreenApi = keyScreenApi,
            onCancel = onCancel
        )

        ReceiveState.Finished -> LaunchedEffect(onCancel) {
            withContext(Dispatchers.Main) {
                onCancel()
            }
        }
    }
}
