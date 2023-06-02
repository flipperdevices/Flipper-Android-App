package com.flipperdevices.faphub.fapscreen.impl.composable.header

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.dialog.composable.multichoice.FlipperMultiChoiceDialog
import com.flipperdevices.core.ui.dialog.composable.multichoice.FlipperMultiChoiceDialogModel
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.faphub.appcard.composable.components.ComposableAppCategory
import com.flipperdevices.faphub.appcard.composable.components.ComposableAppIcon
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
                Column() {
                    ComposableDeleteConfirmDialogBox(
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

@Composable
private fun ComposableDeleteConfirmDialogBox(
    fapItem: FapItem,
    modifier: Modifier = Modifier
) = Row(
    modifier = modifier
        .clip(RoundedCornerShape(12.dp))
        .background(LocalPallet.current.fapHubDeleteDialogBackground),
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically
) {
    ComposableAppIcon(
        modifier = Modifier
            .padding(vertical = 12.dp, horizontal = 8.dp)
            .size(42.dp),
        url = fapItem.picUrl,
        description = fapItem.name
    )
    Column(
        verticalArrangement = Arrangement.spacedBy(1.dp, Alignment.CenterVertically)
    ) {
        Text(
            text = fapItem.name,
            style = LocalTypography.current.bodyM14,
            color = LocalPallet.current.text100
        )
        ComposableAppCategory(category = fapItem.category)
    }
}
