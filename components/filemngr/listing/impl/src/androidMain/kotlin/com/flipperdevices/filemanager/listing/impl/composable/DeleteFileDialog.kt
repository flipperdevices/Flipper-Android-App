package com.flipperdevices.filemanager.listing.impl.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.AnnotatedString
import com.flipperdevices.core.ui.dialog.composable.multichoice.FlipperMultiChoiceDialog
import com.flipperdevices.core.ui.dialog.composable.multichoice.FlipperMultiChoiceDialogModel
import kotlinx.collections.immutable.ImmutableSet
import okio.Path

@Composable
fun DeleteFileDialog(
    paths: ImmutableSet<Path>,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    val dialogModel = remember(onConfirm, onCancel) {
        val title = when (paths.size) {
            1 -> "Delete ${paths.first().name}?"
            else -> "Delete ${paths.size} items?"
        }
        FlipperMultiChoiceDialogModel.Builder()
            .setTitle(title)
            .setDescription(AnnotatedString("This action cannot be undone"))
            .setOnDismissRequest(onCancel)
            .addButton("Delete", onConfirm, isActive = true)
            .addButton("Cancel", onCancel)
            .build()
    }
    FlipperMultiChoiceDialog(model = dialogModel)
}
