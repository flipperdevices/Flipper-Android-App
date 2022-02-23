package com.flipperdevices.filemanager.api.share

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Composable
import java.io.File

interface ShareApi {
    @Composable
    fun AlertDialogDownload(shareFile: ShareFile, onCancel: () -> Unit)

    /**
     * After execute, temporaryFile can be deleted
     */
    suspend fun getExternalUriForFile(
        context: Context,
        temporaryFile: File,
        displayName: String? = null
    ): Uri
}
