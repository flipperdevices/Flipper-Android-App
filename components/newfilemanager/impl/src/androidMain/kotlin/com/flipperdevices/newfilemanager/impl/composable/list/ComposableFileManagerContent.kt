package com.flipperdevices.newfilemanager.impl.composable.list

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.newfilemanager.impl.model.FileItem
import com.flipperdevices.newfilemanager.impl.model.FileManagerState
import flipperapp.components.newfilemanager.impl.generated.resources.Res
import flipperapp.components.newfilemanager.impl.generated.resources.filemanager_empty_folder
import flipperapp.components.newfilemanager.impl.generated.resources.filemanager_error
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource

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
        when (fileManagerState) {
            is FileManagerState.Error -> Text(stringResource(Res.string.filemanager_error))
            is FileManagerState.Ready -> when {
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

                else -> Text(text = stringResource(Res.string.filemanager_empty_folder))
            }
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
        fileManagerState = FileManagerState.Ready(
            "/",
            persistentListOf(
                FileItem.DUMMY_FOLDER,
                FileItem.DUMMY_FILE
            ),
            inProgress = true
        ),
        onFileClick = {}
    )
}
