package com.flipperdevices.selfupdater.googleplay.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.core.ui.dialog.composable.multichoice.FlipperMultiChoiceDialog
import com.flipperdevices.core.ui.dialog.composable.multichoice.FlipperMultiChoiceDialogModel
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.selfupdater.source.googleplay.R

@Composable
internal fun ComposableReadyUpdateDialog(
    onAccept: () -> Unit,
    onDecline: () -> Unit,
) {
    val dialogModel = remember(onDecline, onAccept) {
        FlipperMultiChoiceDialogModel.Builder()
            .setTitle(R.string.ready_update_title)
            .setDescription(R.string.ready_update_text)
            .setOnDismissRequest(onDecline)
            .addButton(R.string.ready_update_button, onAccept, isActive = true)
            .addButton(R.string.ready_update_cancel_button, onDecline)
            .build()
    }
    FlipperMultiChoiceDialog(dialogModel)
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
