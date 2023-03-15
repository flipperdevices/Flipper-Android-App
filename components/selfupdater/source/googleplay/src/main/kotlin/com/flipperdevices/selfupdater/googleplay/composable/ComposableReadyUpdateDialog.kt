package com.flipperdevices.selfupdater.googleplay.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.core.ui.dialog.composable.FlipperDialog
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.selfupdater.source.googleplay.R

@Composable
internal fun ComposableReadyUpdateDialog(
    onAccept: () -> Unit,
    onDecline: () -> Unit,
) {
    FlipperDialog(
        titleId = R.string.ready_update_title,
        textId = R.string.ready_update_text,
        buttonTextId = R.string.ready_update_button,
        onDismissRequest = onDecline,
        onClickButton = onAccept
    )
}

@Preview
@Composable
private fun ComposableReadyUpdateDialogPreview() {
    FlipperThemeInternal {
        ComposableReadyUpdateDialog(
            onAccept = {},
            onDecline = {}
        )
    }
}
