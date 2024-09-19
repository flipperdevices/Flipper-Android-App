package com.flipperdevices.filemanager.impl.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import com.flipperdevices.filemanager.impl.R
import com.flipperdevices.filemanager.impl.composable.dialog.ComposableProgressDialog
import com.flipperdevices.filemanager.impl.composable.list.ComposableFileManagerContent
import com.flipperdevices.filemanager.impl.model.FileManagerState
import com.flipperdevices.filemanager.impl.model.ShareState
import com.flipperdevices.filemanager.impl.model.SpeedState

@Composable
fun ComposableFileManagerUploadedScreen(
    fileManagerState: FileManagerState,
    shareState: ShareState,
    speedState: SpeedState,
    onBack: () -> Unit
) {
    if (shareState is ShareState.Ready && shareState.processCompleted) {
        LaunchedEffect(onBack) {
            onBack()
        }
    }

    ComposableFileManagerUploadedScreenInternal(
        fileManagerState,
        shareState,
        speedState,
        onCancel = onBack
    )
}

@Composable
private fun ComposableFileManagerUploadedScreenInternal(
    fileManagerState: FileManagerState,
    shareState: ShareState,
    speedState: SpeedState,
    onCancel: () -> Unit
) {
    Box {
        ComposableFileManagerContent(fileManagerState = fileManagerState, onFileClick = {})
        ComposableProgressDialog(
            shareState = shareState,
            speedState = speedState,
            onCancel = onCancel
        )
    }
}
