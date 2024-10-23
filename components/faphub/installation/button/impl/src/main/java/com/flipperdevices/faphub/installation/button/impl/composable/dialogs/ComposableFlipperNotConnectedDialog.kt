package com.flipperdevices.faphub.installation.button.impl.composable.dialogs

import androidx.compose.runtime.Composable
import com.flipperdevices.core.ui.dialog.composable.FlipperDialogAndroid
import com.flipperdevices.faphub.installation.button.impl.R
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun ComposableFlipperNotConnectedDialog(
    onDismiss: () -> Unit,
    onOpenDeviceTab: () -> Unit
) {
    FlipperDialogAndroid(
        titleId = R.string.faphub_installation_dialog_flipper_not_connected_title,
        textId = R.string.faphub_installation_dialog_flipper_not_connected_desc,
        buttonTextId = R.string.faphub_installation_dialog_flipper_not_connected_btn,
        onClickButton = {
            onDismiss()
            onOpenDeviceTab()
        },
        imageId = DesignSystem.drawable.ic_flipper_upload_failed,
        onDismissRequest = onDismiss
    )
}
