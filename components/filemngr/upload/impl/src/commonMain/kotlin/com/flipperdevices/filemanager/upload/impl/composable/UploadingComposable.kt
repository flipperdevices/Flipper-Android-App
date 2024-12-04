package com.flipperdevices.filemanager.upload.impl.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.core.ktx.jre.toFormattedSize
import com.flipperdevices.filemanager.ui.components.transfer.FileTransferFullScreenComposable
import com.flipperdevices.filemanager.upload.api.UploaderDecomposeComponent
import flipperapp.components.filemngr.upload.impl.generated.resources.fm_cancel
import flipperapp.components.filemngr.upload.impl.generated.resources.fm_in_progress_file_size
import flipperapp.components.filemngr.upload.impl.generated.resources.fm_in_progress_items
import flipperapp.components.filemngr.upload.impl.generated.resources.fm_in_progress_speed
import flipperapp.components.filemngr.upload.impl.generated.resources.fm_uploading
import flipperapp.components.filemngr.upload.impl.generated.resources.fm_uploading_file
import org.jetbrains.compose.resources.stringResource
import flipperapp.components.filemngr.upload.impl.generated.resources.Res as FUR

@Composable
fun UploadingComposable(
    state: UploaderDecomposeComponent.State.Uploading,
    speed: Long?,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    FileTransferFullScreenComposable(
        modifier = modifier,
        title = stringResource(FUR.string.fm_uploading),
        actionText = stringResource(FUR.string.fm_cancel),
        onActionClick = onCancel,
        progress = if (state.totalSize == 0L) 0f else state.uploadedSize / state.totalSize.toFloat(),
        progressDetailText = when (state.totalItemsAmount) {
            1 -> null
            else -> stringResource(
                FUR.string.fm_uploading_file,
                state.currentItem.fileName,
                state.currentItem.uploadedSize.toFormattedSize(),
                state.currentItem.totalSize.toFormattedSize()
            )
        },
        progressTitle = when {
            state.totalItemsAmount == 1 -> state.currentItem.fileName
            else -> stringResource(
                FUR.string.fm_in_progress_items,
                state.currentItemIndex.plus(1),
                state.totalItemsAmount
            )
        },
        progressText = stringResource(
            FUR.string.fm_in_progress_file_size,
            state.uploadedSize.toFormattedSize(),
            state.totalSize.toFormattedSize()
        ),
        speedText = speed?.let { _ ->
            stringResource(
                FUR.string.fm_in_progress_speed,
                speed.toFormattedSize(),
            )
        },
    )
}
