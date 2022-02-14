package com.flipperdevices.filemanager.impl.composable

import android.text.format.Formatter
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.bridge.dao.api.model.FlipperFileType
import com.flipperdevices.filemanager.impl.R
import com.flipperdevices.filemanager.impl.model.FileItem
import com.flipperdevices.filemanager.impl.model.FileManagerState

@Composable
fun ComposableFileManager(
    fileManagerState: FileManagerState,
    modifier: Modifier = Modifier,
    onFileClick: (FileItem) -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            fileManagerState.filesInDirectory.isNotEmpty() -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(items = fileManagerState.filesInDirectory.toList()) { file ->
                        ComposableFileItem(file, onFileClick)
                    }
                }
            }
            fileManagerState.inProgress -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(size = 48.dp)
                )
            }
            else -> {
                Text(text = stringResource(id = R.string.filemanager_empty_folder))
            }
        }
    }
}

@Composable
private fun ComposableFileItem(fileItem: FileItem, onFileClick: (FileItem) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple()
            ) { onFileClick(fileItem) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        ComposableFileImage(
            modifier = Modifier.padding(all = 8.dp),
            fileItem = fileItem
        )
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            Text(
                modifier = Modifier.padding(end = 8.dp),
                style = MaterialTheme.typography.h5,
                text = fileItem.fileName
            )
            if (!fileItem.isDirectory) {
                val fileSize = Formatter.formatFileSize(LocalContext.current, fileItem.size)
                Text(
                    style = MaterialTheme.typography.h5,
                    text = fileSize
                )
            }
        }
    }
}

@Composable
private fun ComposableFileImage(modifier: Modifier, fileItem: FileItem) {
    if (fileItem.isDirectory) {
        Image(
            modifier = modifier,
            painter = painterResource(R.drawable.ic_folder),
            contentDescription = stringResource(R.string.filemanager_folder_pic_desc)
        )
    } else {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Image(
                modifier = Modifier.size(size = 48.dp),
                painter = painterResource(R.drawable.ic_file),
                contentDescription = stringResource(R.string.filemanager_file_pic_desc)
            )
            val fileIcon = remember(fileItem) {
                FlipperFileType.getByExtension(
                    fileItem.fileName.substringAfterLast(".")
                )
            }

            if (fileIcon != null) {
                Image(
                    modifier = Modifier.size(size = 24.dp),
                    painter = painterResource(fileIcon.icon),
                    contentDescription = fileIcon.humanReadableName
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun ComposableFileManagerPreview() {
    ComposableFileManager(
        FileManagerState(
            "/",
            setOf(
                FileItem.DUMMY_FOLDER,
                FileItem.DUMMY_FILE
            )
        )
    ) {}
}
