package com.flipperdevices.selfupdater.googleplay.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.core.ui.dialog.composable.multichoice.FlipperMultiChoiceDialog
import com.flipperdevices.core.ui.dialog.composable.multichoice.FlipperMultiChoiceDialogModel
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.selfupdater.source.googleplay.R

@Composable
internal fun ComposablePendingUpdateDialog(
    onAccept: () -> Unit,
    onDecline: () -> Unit,
) {
    val dialogModel = remember(onDecline, onAccept) {
        FlipperMultiChoiceDialogModel.Builder()
            .setTitle(R.string.pending_update_title)
            .setDescription(R.string.pending_update_desc)
            .setOnDismissRequest(onDecline)
            .addButton(R.string.pending_update_accept, onAccept, isActive = true)
            .addButton(R.string.pending_update_decline, onDecline)
            .build()
    }
    FlipperMultiChoiceDialog(model = dialogModel)
}

@Preview
@Composable
private fun ComposablePendingUpdateDialogPreview() {
    FlipperThemeInternal {
        ComposablePendingUpdateDialog(
            onAccept = {},
            onDecline = {}
        )
    }
}
