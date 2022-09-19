package com.flipperdevices.core.ui.dialog.composable.multichoice

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.flipperdevices.core.ui.theme.LocalPallet

@Composable
fun FlipperMultiChoiceDialog(
    modifier: Modifier = Modifier,
    model: FlipperMultiChoiceDialogModel,
) {
    Dialog(onDismissRequest = {
        if (model.closeOnClickOutside) {
            model.onDismissRequest?.invoke()
        }
    }) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(18.dp))
                .background(LocalPallet.current.backgroundDialog)
        ) {
            FlipperMultiChoiceDialogContent(model)
        }
    }
}

