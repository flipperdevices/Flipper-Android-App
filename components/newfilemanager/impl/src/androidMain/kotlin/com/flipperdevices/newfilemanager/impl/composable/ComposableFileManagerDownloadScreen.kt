package com.flipperdevices.newfilemanager.impl.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.flipperdevices.newfilemanager.impl.composable.dialog.ComposableProgressDialog
import com.flipperdevices.newfilemanager.impl.composable.list.ComposableFileManagerContent
import com.flipperdevices.newfilemanager.impl.model.FileManagerState
import com.flipperdevices.newfilemanager.impl.model.ShareState
import com.flipperdevices.newfilemanager.impl.model.SpeedState
@Composable
fun ComposableFileManagerDownloadScreen(
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

    ComposableFileManagerDownloadScreenInternal(
        fileManagerState,
        shareState,
        onCancel = onBack,
        speedState = speedState
    )
}

@Composable
private fun ComposableFileManagerDownloadScreenInternal(
    fileManagerState: FileManagerState,
    shareState: ShareState,
    speedState: SpeedState,
    onCancel: () -> Unit
) {
    Box {
        ComposableFileManagerContent(fileManagerState = fileManagerState, onFileClick = {})
        ComposableProgressDialog(
            shareState = shareState,
            onCancel = onCancel,
            speedState = speedState
        )
    }
}
