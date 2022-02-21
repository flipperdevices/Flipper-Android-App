package com.flipperdevices.share.receive.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.core.ui.composable.ComposableFlipperButton
import com.flipperdevices.share.receive.R

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
fun ComposableKeyReceive(onSave: () -> Unit = {}) {
    ComposableFlipperButton(
        text = stringResource(R.string.receive_save_btn),
        onClick = onSave
    )
}
