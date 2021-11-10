package com.flipperdevices.share.api

import androidx.compose.runtime.Composable
import com.flipperdevices.share.model.ShareFile

interface ShareApi {
    @Composable
    fun AlertDialogDownload(shareFile: ShareFile, onCancel: () -> Unit)
}
