package com.flipperdevices.remotecontrols.impl.setup.api.dialog.composable

import androidx.compose.runtime.Composable
import com.flipperdevices.core.ui.dialog.composable.FlipperDialogAndroid
import com.flipperdevices.remotecontrols.setup.impl.R
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun ComposableFlipperNotConnectedDialog(
    onDismiss: () -> Unit,
    onOpenDeviceTab: () -> Unit
) {
    FlipperDialogAndroid(
        titleId = R.string.remotecontrols_dialog_flipper_not_connected_title,
        textId = R.string.remotecontrols_dialog_flipper_not_connected_desc,
        buttonTextId = R.string.remotecontrols_dialog_flipper_not_connected_btn,
        onClickButton = {
            onOpenDeviceTab()
            onDismiss()
        },
        imageId = DesignSystem.drawable.ic_flipper_upload_failed,
        onDismissRequest = onDismiss
    )
}
