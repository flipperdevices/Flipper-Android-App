package com.flipperdevices.faphub.installation.button.impl.composable.dialogs

import androidx.compose.runtime.Composable
import com.flipperdevices.core.ui.dialog.composable.FlipperDialog
import com.flipperdevices.faphub.installation.button.impl.R
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun ComposableFlipperBusy(
    showBusyDialog: Boolean,
    onDismiss: () -> Unit,
) {
    if (showBusyDialog.not()) {
        return
    }

    FlipperDialog(
        imageId = DesignSystem.drawable.pic_flipper_is_busy,
        titleId = R.string.faphub_load_fap_dialog_flipper_busy,
        textId = R.string.faphub_load_fap_dialog_flipper_busy_desc,
        buttonTextId = R.string.faphub_load_fap_dialog_flipper_busy_btn,
        onClickButton = onDismiss,
        onDismissRequest = onDismiss
    )
}
