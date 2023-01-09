package com.flipperdevices.share.receive.composable.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed
import com.flipperdevices.core.ui.ktx.ComposableFlipperButton
import com.flipperdevices.keyscreen.api.KeyScreenApi
import com.flipperdevices.share.receive.R
import com.flipperdevices.share.receive.composable.components.ComposableKeySaveBar
import com.flipperdevices.share.receive.composable.components.ComposableKeySaveFooter

@Composable
fun ComposableKeySaveScreen(
    keyScreenApi: KeyScreenApi,
    keyParsed: FlipperKeyParsed,
    savingInProgress: Boolean,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        ComposableKeySaveBar(onCancel)
        keyScreenApi.KeyCard(
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
            key = keyParsed,
            deleted = false
        )
        ComposableKeySaveFooter {
            if (savingInProgress) {
                Box(
                    modifier = Modifier
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                ComposableFlipperButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.receive_save_btn),
                    onClick = onSave
                )
            }
        }
    }
}
