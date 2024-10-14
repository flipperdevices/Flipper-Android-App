package com.flipperdevices.faphub.uninstallbutton.impl.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.dialog.composable.multichoice.FlipperMultiChoiceDialog
import com.flipperdevices.core.ui.dialog.composable.multichoice.FlipperMultiChoiceDialogModel
import com.flipperdevices.core.ui.dialog.composable.multichoice.addButton
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.faphub.uninstallbutton.impl.R

@Composable
fun ComposableDeleteConfirmDialog(
    dialogAppBox: @Composable (Modifier) -> Unit,
    onConfirmDelete: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val deleteText = stringResource(R.string.faphub_delete_dialog_btn)
    val deleteColor = LocalPallet.current.onError
    val config = remember(deleteText, deleteColor) {
        FlipperMultiChoiceDialogModel.Builder()
            .addButton(
                text = deleteText,
                onClick = {
                    onConfirmDelete()
                    onDismiss()
                },
                textColor = deleteColor
            )
            .addButton(textId = R.string.faphub_delete_dialog_cancel, onClick = onDismiss)
            .setOnDismissRequest(onDismiss)
            .setDescription(
                content = {
                    Column {
                        dialogAppBox(
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 24.dp)
                        )
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp),
                            text = stringResource(R.string.faphub_delete_dialog_title),
                            textAlign = TextAlign.Center,
                            color = LocalPallet.current.text100,
                            style = LocalTypography.current.bodyM14
                        )
                    }
                }
            )
            .build()
    }
    FlipperMultiChoiceDialog(model = config, modifier = modifier)
}
