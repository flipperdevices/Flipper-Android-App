package com.flipperdevices.filemanager.listing.impl.composable.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.flipperdevices.core.ui.dialog.composable.multichoice.FlipperMultiChoiceDialog
import com.flipperdevices.core.ui.dialog.composable.multichoice.FlipperMultiChoiceDialogModel
import com.flipperdevices.filemanager.listing.impl.viewmodel.DeleteFilesViewModel
import flipperapp.components.filemngr.listing.impl.generated.resources.fml_dialog_cancel_btn
import flipperapp.components.filemngr.listing.impl.generated.resources.fml_dialog_delete_btn
import flipperapp.components.filemngr.listing.impl.generated.resources.fml_dialog_delete_file
import flipperapp.components.filemngr.listing.impl.generated.resources.fml_dialog_delete_files
import flipperapp.components.filemngr.listing.impl.generated.resources.fml_dialog_desc
import kotlinx.collections.immutable.ImmutableSet
import okio.Path
import org.jetbrains.compose.resources.stringResource
import flipperapp.components.filemngr.listing.impl.generated.resources.Res as FML

@Composable
fun DeleteFileDialog(
    paths: ImmutableSet<Path>,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    val dialogModel = remember(onConfirm, onCancel) {
        FlipperMultiChoiceDialogModel.Builder()
            .setTitle(
                composableText = {
                    when (paths.size) {
                        1 -> stringResource(
                            FML.string.fml_dialog_delete_file,
                            paths.first().name
                        )

                        else -> stringResource(
                            FML.string.fml_dialog_delete_files,
                            paths.size.toString()
                        )
                    }
                }
            )
            .setDescription(composableText = { stringResource(FML.string.fml_dialog_desc) })
            .setOnDismissRequest(onCancel)
            .addButton(
                textComposable = { stringResource(FML.string.fml_dialog_delete_btn) },
                onClick = onConfirm,
                isActive = true
            )
            .addButton(
                textComposable = { stringResource(FML.string.fml_dialog_cancel_btn) },
                onClick = onCancel
            )
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
