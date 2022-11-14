package com.flipperdevices.share.receive.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed
import com.flipperdevices.keyscreen.api.KeyScreenApi

@Composable
fun ComposableKeySaveScreen(
    keyScreenApi: KeyScreenApi,
    keyParsed: FlipperKeyParsed,
    savingInProgress: Boolean,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    Column {
        ComposableKeySaveBar(onCancel)
        keyScreenApi.KeyCard(
            key = keyParsed,
            deleted = false
        )
        ComposableKeySaveFooter(savingInProgress, onSave)
    }
}
