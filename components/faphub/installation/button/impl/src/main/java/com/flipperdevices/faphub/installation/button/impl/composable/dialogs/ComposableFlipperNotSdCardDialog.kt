package com.flipperdevices.faphub.installation.button.impl.composable.dialogs

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import com.flipperdevices.core.ui.dialog.composable.FlipperDialogAndroid
import com.flipperdevices.faphub.installation.button.impl.R
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun ComposableFlipperNotSdCardDialog(
    onDismiss: () -> Unit
) {
    FlipperDialogAndroid(
        titleId = R.string.faphub_installation_dialog_no_sd_title,
        textId = R.string.faphub_installation_dialog_no_sd_desc,
        buttonTextId = R.string.faphub_installation_dialog_no_sd_btn,
        onClickButton = onDismiss,
        imageId = if (MaterialTheme.colors.isLight) {
            DesignSystem.drawable.ic_no_sd
        } else {
            DesignSystem.drawable.ic_no_sd_dark
        },
        onDismissRequest = onDismiss
    )
}
