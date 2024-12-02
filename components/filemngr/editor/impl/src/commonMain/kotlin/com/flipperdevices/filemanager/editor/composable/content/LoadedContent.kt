package com.flipperdevices.filemanager.editor.composable.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.filemanager.editor.viewmodel.EditorViewModel

@Composable
fun LoadedContent(
    state: EditorViewModel.State,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        TextField(
            modifier = Modifier.fillMaxSize(),
            value = state.hexString.content,
            enabled = state.canEdit,
            readOnly = !state.canEdit,
            onValueChange = onTextChange,
            colors = TextFieldDefaults.textFieldColors(
                cursorColor = LocalPallet.current.text100
            )
        )
    }
}
