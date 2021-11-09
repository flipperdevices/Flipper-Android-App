package com.flipperdevices.share.impl.composable

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.share.impl.viewmodel.ShareViewModel
import com.flipperdevices.share.impl.viewmodel.ShareViewModelFactory
import com.flipperdevices.share.model.ShareFile

@Composable
fun ComposableShare(
    shareFile: ShareFile,
    onCancel: () -> Unit,
    viewModel: ShareViewModel = viewModel(
        key = shareFile.flipperFilePath,
        factory = ShareViewModelFactory(
            shareFile,
            LocalContext.current.applicationContext as Application
        )
    )
) {
    val downloadProgress by viewModel.getDownloadProgress()
    ComposableAlertDialog(
        shareFile = shareFile,
        downloadProgress = downloadProgress,
        onCancel = {
            viewModel.cancelDownload()
            onCancel()
        }
    )
}
