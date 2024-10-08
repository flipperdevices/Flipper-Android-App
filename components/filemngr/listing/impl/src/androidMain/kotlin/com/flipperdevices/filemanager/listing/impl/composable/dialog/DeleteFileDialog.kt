package com.flipperdevices.filemanager.listing.impl.composable.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import com.flipperdevices.core.ui.dialog.composable.multichoice.FlipperMultiChoiceDialog
import com.flipperdevices.core.ui.dialog.composable.multichoice.FlipperMultiChoiceDialogModel
import com.flipperdevices.filemanager.listing.impl.viewmodel.DeleteFilesViewModel
import kotlinx.collections.immutable.ImmutableSet
import okio.Path
import com.flipperdevices.filemanager.listing.impl.R as FML

@Composable
fun DeleteFileDialog(
    paths: ImmutableSet<Path>,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val dialogModel = remember(onConfirm, onCancel) {
        val title = when (paths.size) {
            1 -> context.getString(
                FML.string.fml_dialog_delete_file,
                paths.first().name
            )

            else -> context.getString(
                FML.string.fml_dialog_delete_files,
                paths.size.toString()
            )
        }
        FlipperMultiChoiceDialogModel.Builder()
            .setTitle(title)
            .setDescription(AnnotatedString(context.getString(FML.string.fml_dialog_desc)))
            .setOnDismissRequest(onCancel)
            .addButton(
                context.getString(FML.string.fml_dialog_delete_btn),
                onConfirm,
                isActive = true
            )
            .addButton(context.getString(FML.string.fml_dialog_cancel_btn), onCancel)
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
