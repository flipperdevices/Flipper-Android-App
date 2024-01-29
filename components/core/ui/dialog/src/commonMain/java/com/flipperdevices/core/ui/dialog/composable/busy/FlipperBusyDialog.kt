package com.flipperdevices.core.ui.dialog.composable.busy

import androidx.compose.runtime.Composable
import com.flipperdevices.core.ui.dialog.R
import com.flipperdevices.core.ui.dialog.composable.FlipperDialog

@Composable
fun ComposableFlipperBusy(
    onDismiss: () -> Unit,
    goToRemote: () -> Unit
) {
    FlipperDialog(
        imageId = R.drawable.pic_flipper_is_busy,
        titleId = R.string.core_ui_dialog_flipper_busy_title,
        textId = R.string.core_ui_dialog_flipper_busy_desc,
        buttonTextId = R.string.core_ui_dialog_flipper_busy_action,
        onDismissRequest = onDismiss,
        onClickButton = {
            onDismiss()
            goToRemote()
        }
    )
}
