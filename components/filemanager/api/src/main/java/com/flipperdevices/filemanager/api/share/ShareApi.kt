package com.flipperdevices.filemanager.api.share

import androidx.compose.runtime.Composable

interface ShareApi {
    @Composable
    fun AlertDialogDownload(shareFile: ShareFile, onCancel: () -> Unit)
}
