package com.flipperdevices.filemanager.impl.composable

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.flipperdevices.deeplink.api.DeepLinkParser
import com.flipperdevices.deeplink.model.DeeplinkContent
import com.flipperdevices.filemanager.impl.composable.bar.ComposableFileManagerTopBar
import com.flipperdevices.filemanager.impl.composable.list.ComposableFileManagerContent
import com.flipperdevices.filemanager.impl.model.FileItem
import com.flipperdevices.filemanager.impl.model.FileManagerState
import kotlinx.coroutines.runBlocking

@Composable
fun ComposableFileManagerScreen(
    fileManagerState: FileManagerState,
    onOpenFolder: (FileItem) -> Unit,
    deepLinkParser: DeepLinkParser,
    onUploadFile: (DeeplinkContent) -> Unit
) {
    val context = LocalContext.current

    val pickFileLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            runBlocking {
                val deeplinkContent = deepLinkParser.fromUri(context, uri)?.content
                if (deeplinkContent != null) {
                    onUploadFile(deeplinkContent)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            ComposableFileManagerTopBar(fileManagerState.currentPath) {
                pickFileLauncher.launch("*/*")
            }
        }
    ) { scaffoldPaddings ->
        ComposableFileManagerContent(
            Modifier.padding(scaffoldPaddings),
            fileManagerState,
            onOpenFolder
        )
    }
}
