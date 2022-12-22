package com.flipperdevices.share.receive.composable.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.ComposableFlipperButton
import com.flipperdevices.core.ui.ktx.placeholderConnecting
import com.flipperdevices.keyscreen.api.KeyScreenApi
import com.flipperdevices.share.receive.R
import com.flipperdevices.share.receive.composable.components.ComposableKeySaveBar
import com.flipperdevices.share.receive.composable.components.ComposableKeySaveFooter

@Composable
internal fun ComposableKeyInProgressScreen(keyScreenApi: KeyScreenApi, onCancel: () -> Unit) {
    Column {
        ComposableKeySaveBar(onCancel)
        keyScreenApi.KeyCardLoading(Modifier.padding(start = 24.dp, end = 24.dp, bottom = 24.dp))
        ComposableKeySaveFooter(
            Modifier.padding(horizontal = 24.dp).placeholderConnecting(shape = 30)
        ) { ComposableKeySaveFooterContent() }
    }
}

@Composable
private fun ComposableKeySaveFooterContent() {
    ComposableFlipperButton(
        text = stringResource(R.string.receive_save_btn),
        onClick = {}
    )
}
