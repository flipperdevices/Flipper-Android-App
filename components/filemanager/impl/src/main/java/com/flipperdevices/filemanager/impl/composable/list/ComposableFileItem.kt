package com.flipperdevices.filemanager.impl.composable.list

import android.text.format.Formatter
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.bridge.dao.api.model.iconId
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.filemanager.impl.R
import com.flipperdevices.filemanager.impl.model.FileItem

@Composable
fun ComposableFileItem(
    fileItem: FileItem,
    onFileClick: (FileItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickableRipple { onFileClick(fileItem) },
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
fun ComposableFileImage(
    fileItem: FileItem,
    modifier: Modifier = Modifier
) {
    if (fileItem.isDirectory) {
        Image(
            modifier = modifier,
            painter = painterResource(R.drawable.ic_folder),
            contentDescription = stringResource(
                com.flipperdevices.filemanager.impl.R.string.filemanager_folder_pic_desc
            )
        )
    } else {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Image(
                modifier = Modifier.size(size = 48.dp),
                painter = painterResource(R.drawable.ic_file),
                contentDescription = stringResource(
                    com.flipperdevices.filemanager.impl.R.string.filemanager_file_pic_desc
                )
            )
            val fileIcon = remember(fileItem) {
                FlipperKeyType.getByExtension(
                    fileItem.fileName.substringAfterLast(".")
                )
            }

            if (fileIcon != null) {
                Image(
                    modifier = Modifier.size(size = 24.dp),
                    painter = painterResource(fileIcon.iconId),
                    contentDescription = fileIcon.humanReadableName
                )
            }
        }
    }
}
