package com.flipperdevices.share.receive.composable

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
import com.flipperdevices.core.ui.composable.ComposableFlipperButton
import com.flipperdevices.keyscreen.api.KeyScreenApi
import com.flipperdevices.share.receive.R

@Composable
fun ComposableKeySaveScreen(
    keyScreenApi: KeyScreenApi,
    keyParsed: FlipperKeyParsed,
    savingInProgress: Boolean,
    onSave: () -> Unit
) {
    Column {
        Box(modifier = Modifier.weight(1f)) {
            keyScreenApi.KeyCard(key = keyParsed)
        }

        SaveButton(savingInProgress, onSave)
    }
}

@Composable
private fun SaveButton(savingInProgress: Boolean, onSave: () -> Unit) {
    if (savingInProgress) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    ComposableFlipperButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        text = stringResource(R.string.receive_save_btn),
        onClick = onSave
    )
}
