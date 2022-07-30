package com.flipperdevices.filemanager.impl.composable

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.flipperdevices.deeplink.api.DeepLinkParser
import com.flipperdevices.deeplink.model.DeeplinkContent
import com.flipperdevices.filemanager.impl.R
import com.flipperdevices.filemanager.impl.composable.bar.ComposableEllipsizeStartText
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

@Composable
private fun ComposableFileManagerTopBar(path: String, onClickSaveButton: () -> Unit) {
    TopAppBar(
        title = {
            ComposableEllipsizeStartText(
                text = path
            )
        },
        actions = {
            if (isAbleToSafe(path)) {
                IconButton(onClick = onClickSaveButton) {
                    Icon(
                        painter = painterResource(
                            com.flipperdevices.core.ui.res.R.drawable.ic_upload
                        ),
                        contentDescription = stringResource(R.string.filemanager_upload_action)
                    )
                }
            }
        }
    )
}

private fun isAbleToSafe(path: String) = path.startsWith("/ext")
