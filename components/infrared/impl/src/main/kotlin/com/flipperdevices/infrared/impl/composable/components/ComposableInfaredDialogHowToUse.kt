package com.flipperdevices.infrared.impl.composable.components

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import com.flipperdevices.core.ui.dialog.composable.FlipperDialogAndroid
import com.flipperdevices.infrared.impl.R

@Composable
internal fun ComposableInfraredDialogHowToUse(
    isShowDialog: Boolean,
    onClose: () -> Unit,
) {
    if (!isShowDialog) {
        return
    }

    val imageId = if (MaterialTheme.colors.isLight) {
        R.drawable.pic_how_to_use_light
    } else {
        R.drawable.pic_how_to_use_dark
    }

    FlipperDialogAndroid(
        buttonTextId = R.string.infrared_dialog_how_to_use_btn,
        onClickButton = onClose,
        onDismissRequest = onClose,
        imageId = imageId,
        titleId = R.string.infrared_dialog_how_to_use_title,
        textId = R.string.infrared_dialog_how_to_use_text,
    )
}
