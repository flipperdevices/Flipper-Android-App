package com.flipperdevices.faphub.fapscreen.impl.composable.header

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
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.faphub.dao.api.model.FapItem
import com.flipperdevices.faphub.fapscreen.impl.R

@Composable
fun ComposableDeleteConfirmDialog(
    fapItem: FapItem,
    onConfirmDelete: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val deleteText = stringResource(R.string.fapscreen_dialog_delete_btn)
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
            .addButton(textId = R.string.fapscreen_dialog_delete_cancel, onClick = onDismiss)
            .setOnDismissRequest(onDismiss)
            .setDescription {
                Column {
                    ComposableAppDialogBox(
                        fapItem = fapItem,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 24.dp)
                    )
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        text = stringResource(R.string.fapscreen_dialog_delete_title),
                        textAlign = TextAlign.Center,
                        color = LocalPallet.current.text100,
                        style = LocalTypography.current.bodyM14
                    )
                }
            }
            .build()
    }
    FlipperMultiChoiceDialog(model = config, modifier = modifier)
}
