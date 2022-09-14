package com.flipperdevices.share.receive.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed
import com.flipperdevices.keyscreen.api.KeyScreenApi
import com.flipperdevices.keyscreen.shared.bar.ComposableBarCancelIcon
import com.flipperdevices.keyscreen.shared.bar.ComposableBarTitle
import com.flipperdevices.keyscreen.shared.bar.ComposableKeyScreenAppBar
import com.flipperdevices.share.receive.R

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
        keyScreenApi.KeyCard(key = keyParsed, deleted = false)
        ComposableKeySaveFooter(savingInProgress, onSave)
    }
}

@Composable
fun ComposableKeySaveBar(onBack: () -> Unit) {
    ComposableKeyScreenAppBar(
        centerBlock = { ComposableBarTitle(modifier = it, textId = R.string.receive_title) },
        endBlock = { ComposableBarCancelIcon(modifier = it, onClick = onBack) }
    )
}
