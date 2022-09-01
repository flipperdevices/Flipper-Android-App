package com.flipperdevices.keyscreen.emulate.composable.common

import androidx.compose.runtime.Composable
import com.flipperdevices.core.ui.dialog.composable.FlipperDialog
import com.flipperdevices.core.ui.res.R as DesignSystem
import com.flipperdevices.keyscreen.emulate.R

@Composable
fun ComposableAlreadyOpenedAppDialog(onCloseDialog: () -> Unit) {
    FlipperDialog(
        imageId = DesignSystem.drawable.pic_flipper_is_busy,
        titleId = R.string.already_open_dialog_title,
        textId = R.string.already_open_dialog_desc,
        buttonTextId = R.string.already_open_dialog_btn,
        onClickButton = onCloseDialog,
        onDismissRequest = onCloseDialog
    )
}
