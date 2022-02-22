package com.flipperdevices.share.receive.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.flipperdevices.keyscreen.api.KeyScreenApi
import com.flipperdevices.share.receive.model.ReceiveState

@Composable
fun ComposableKeyReceive(
    keyScreenApi: KeyScreenApi,
    state: ReceiveState,
    onSave: () -> Unit = {}
) {
    when (state) {
        ReceiveState.NotStarted -> ComposableProgress()
        is ReceiveState.Pending -> ComposableKeySaveScreen(
            keyScreenApi = keyScreenApi,
            keyParsed = state.parsed,
            savingInProgress = false,
            onSave = onSave
        )
        is ReceiveState.Saving -> ComposableKeySaveScreen(
            keyScreenApi = keyScreenApi,
            keyParsed = state.parsed,
            savingInProgress = true,
            onSave = onSave
        )
        ReceiveState.Finished -> return
    }
}

@Composable
fun ComposableProgress() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
