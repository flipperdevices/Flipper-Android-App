package com.flipperdevices.filemanager.impl.composable.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.filemanager.impl.R
import com.flipperdevices.filemanager.impl.model.FileItem
import com.flipperdevices.filemanager.impl.model.FileManagerState
import kotlinx.collections.immutable.persistentSetOf

@Composable
fun ComposableFileManagerContent(
    fileManagerState: FileManagerState,
    onFileClick: (FileItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            fileManagerState.filesInDirectory.isNotEmpty() ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(items = fileManagerState.filesInDirectory.toList()) { file ->
                        ComposableFileItem(file, onFileClick)
                    }
                }
            fileManagerState.inProgress ->
                CircularProgressIndicator(modifier = Modifier.size(size = 48.dp))
            else -> Text(text = stringResource(id = R.string.filemanager_empty_folder))
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun ComposableFileManagerPreview() {
    ComposableFileManagerContent(
        fileManagerState = FileManagerState(
            "/",
            persistentSetOf(
                FileItem.DUMMY_FOLDER,
                FileItem.DUMMY_FILE
            )
        ),
        onFileClick = {}
    )
}
