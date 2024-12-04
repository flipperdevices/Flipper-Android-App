package com.flipperdevices.filemanager.editor.composable

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.flipperdevices.filemanager.editor.composable.content.LoadedContent
import com.flipperdevices.filemanager.editor.viewmodel.EditorViewModel

@Composable
fun FileManagerEditorComposable(
    editorViewModel: EditorViewModel,
    onBack: () -> Unit,
    onSaveAsClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val editorState by editorViewModel.state.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            EditorAppBar(
                path = editorState.fullPathOnFlipper,
                onSaveClick = onSaveClick::invoke,
                onSaveAsClick = onSaveAsClick,
                onBack = onBack::invoke,
                editorEncodingEnum = editorState.encoding,
                canSave = editorState.canEdit,
                onEditorTabChange = editorViewModel::onEditorTypeChange
            )
        }
    ) { contentPadding ->
        LoadedContent(
            state = editorState,
            onTextChange = editorViewModel::onTextChanged,
            modifier = Modifier.padding(contentPadding)
        )
    }
}
