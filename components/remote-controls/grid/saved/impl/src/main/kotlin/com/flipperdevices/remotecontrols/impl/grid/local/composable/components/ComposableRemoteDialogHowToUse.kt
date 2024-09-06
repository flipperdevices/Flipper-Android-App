package com.flipperdevices.remotecontrols.impl.grid.local.composable.components

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import com.flipperdevices.core.ui.dialog.composable.FlipperDialog
import com.flipperdevices.remotecontrols.grid.saved.impl.R

@Composable
internal fun ComposableRemoteDialogHowToUse(
    isShowDialog: Boolean,
    onClose: () -> Unit,
) {
    if (!isShowDialog) {
        return
    }

    val imageId = if (MaterialTheme.colors.isLight) {
        R.drawable.ir_flipper_format_light
    } else {
        R.drawable.ir_flipper_format_dark
    }

    FlipperDialog(
        buttonTextId = R.string.rc_dialog_how_to_use_btn,
        onClickButton = onClose,
        onDismissRequest = onClose,
        imageId = imageId,
        titleId = R.string.rc_dialog_how_to_use_title,
        textId = R.string.rc_dialog_how_to_use_text,
    )
}
