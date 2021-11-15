package com.flipperdevices.filemanager.impl.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.flipperdevices.filemanager.impl.model.FileItem
import com.flipperdevices.filemanager.impl.model.FileManagerState
import com.flipperdevices.share.api.ShareApi
import com.flipperdevices.share.model.ShareFile

@Composable
fun ComposableFileManagerWithDialog(
    fileManagerState: FileManagerState,
    shareApi: ShareApi,
    onDirectoryClick: (FileItem) -> Unit
) {
    var sharedFile by remember { mutableStateOf<FileItem?>(null) }

    ComposableFileManager(fileManagerState) { itemFile ->
        if (itemFile.isDirectory) {
            onDirectoryClick(itemFile)
        } else {
            sharedFile = itemFile
        }
    }

    if (sharedFile != null) {
        shareApi.AlertDialogDownload(
            ShareFile(
                name = sharedFile!!.fileName,
                flipperFilePath = sharedFile!!.path,
                size = sharedFile!!.size
            )
        ) {
            sharedFile = null
        }
    }
}
