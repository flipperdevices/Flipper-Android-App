package com.flipperdevices.filemanager.editor.composable

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.flipperdevices.filemanager.editor.composable.content.EditorLoadingContent
import com.flipperdevices.filemanager.editor.composable.content.ErrorContent
import com.flipperdevices.filemanager.editor.composable.content.LoadedContent
import com.flipperdevices.filemanager.editor.viewmodel.EditorViewModel
import com.flipperdevices.filemanager.editor.viewmodel.FileNameViewModel
import com.flipperdevices.filemanager.upload.api.UploaderDecomposeComponent
import okio.Path

@Composable
fun FileManagerEditorComposable(
    path: Path,
    editorViewModel: EditorViewModel,
    uploaderDecomposeComponent: UploaderDecomposeComponent,
    fileNameViewModel: FileNameViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val editorState by editorViewModel.state.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            EditorAppBar(
                path = path,
                onSaveClick = onSaveClick@{
                    val rawContent = editorViewModel.getRawContent() ?: return@onSaveClick
                    uploaderDecomposeComponent.uploadRaw(
                        folderPath = path.parent ?: return@onSaveClick,
                        fileName = path.name,
                        content = rawContent
                    )
                },
                onSaveAsClick = {
                    fileNameViewModel.show()
                },
                onBack = onBack::invoke,
                editorEncodingEnum = (editorState as? EditorViewModel.State.Loaded)?.encoding,
                canSave = (editorState is EditorViewModel.State.Loaded),
                onEditorTabChange = editorViewModel::onEditorTypeChange
            )
        }
    ) { contentPadding ->
        when (val localEditorState = editorState) {
            EditorViewModel.State.Error -> {
                ErrorContent(
                    modifier = Modifier.padding(contentPadding)
                )
            }

            EditorViewModel.State.Preparing,
            is EditorViewModel.State.Loading -> {
                EditorLoadingContent(
                    modifier = Modifier.padding(contentPadding)
                )
            }

            is EditorViewModel.State.Loaded -> {
                LoadedContent(
                    state = localEditorState,
                    onTextChange = editorViewModel::onTextChanged,
                    modifier = Modifier.padding(contentPadding)
                )
            }

            is EditorViewModel.State.Saving -> Unit
        }
    }
}
