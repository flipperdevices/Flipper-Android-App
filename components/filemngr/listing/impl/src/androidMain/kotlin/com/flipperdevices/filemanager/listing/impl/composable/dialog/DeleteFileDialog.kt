package com.flipperdevices.filemanager.listing.impl.composable.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.AnnotatedString
import com.flipperdevices.core.ui.dialog.composable.multichoice.FlipperMultiChoiceDialog
import com.flipperdevices.core.ui.dialog.composable.multichoice.FlipperMultiChoiceDialogModel
import com.flipperdevices.filemanager.listing.impl.viewmodel.DeleteFilesViewModel
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

@Composable
fun DeleteFileDialog(
    deleteFileState: DeleteFilesViewModel.State,
    deleteFileViewModel: DeleteFilesViewModel
) {
    when (val state = deleteFileState) {
        is DeleteFilesViewModel.State.Confirm -> {
            DeleteFileDialog(
                paths = state.paths,
                onCancel = deleteFileViewModel::onCancel,
                onConfirm = deleteFileViewModel::onDeleteConfirm
            )
        }

        else -> Unit
    }
}
