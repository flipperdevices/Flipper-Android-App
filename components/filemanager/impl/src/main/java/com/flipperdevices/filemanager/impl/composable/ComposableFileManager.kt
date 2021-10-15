package com.flipperdevices.filemanager.impl.composable

import android.text.format.Formatter
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.filemanager.impl.R
import com.flipperdevices.filemanager.impl.model.FileItem


@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun ComposableFileManager(files: List<FileItem> = listOf(FileItem.DUMMY)) {
    LazyColumn {
        items(files) { file ->
            ComposableFileItem(file)
        }
    }
}

@Composable
fun ComposableFileItem(fileItem: FileItem) {
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ComposableFileImage(
            modifier = Modifier.padding(all = 8.dp),
            fileItem = fileItem
        )
        Column {
            Text(
                style = MaterialTheme.typography.h5,
                text = fileItem.fileName
            )
            val fileSize = Formatter.formatFileSize(LocalContext.current, fileItem.size)
            Text(
                style = MaterialTheme.typography.h5,
                text = fileSize
            )
        }
    }
}

@Composable
fun ComposableFileImage(modifier: Modifier, fileItem: FileItem) {
    if (fileItem.isDirectory) {
        Image(
            modifier = modifier,
            painter = painterResource(R.drawable.ic_folder),
            contentDescription = stringResource(R.string.filemanager_folder_pic_desc)
        )
    } else {
        Image(
            modifier = modifier,
            painter = painterResource(R.drawable.ic_file),
            contentDescription = stringResource(R.string.filemanager_file_pic_desc)
        )
    }
}