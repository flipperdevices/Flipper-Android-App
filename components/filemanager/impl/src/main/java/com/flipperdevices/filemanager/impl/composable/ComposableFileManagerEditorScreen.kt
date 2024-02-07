package com.flipperdevices.filemanager.impl.composable

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ktx.jre.roundPercentToString
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.filemanager.impl.R
import com.flipperdevices.filemanager.impl.composable.bar.ComposableEditorTopBar
import com.flipperdevices.filemanager.impl.model.DownloadProgress
import com.flipperdevices.filemanager.impl.model.EditorState

@Composable
fun ComposableFileManagerEditorScreen(
    editorState: EditorState,
    onClickSaveButton: (String) -> Unit,
    onBack: () -> Unit
) {
    if (editorState is EditorState.Saved) {
        LaunchedEffect(onBack) {
            onBack()
        }
    }

    ComposableFileManagerEditorScreenInternal(
        editorState = editorState,
        onClickSaveButton = onClickSaveButton
    )
}

@Composable
private fun ComposableFileManagerEditorScreenInternal(
    editorState: EditorState,
    onClickSaveButton: (String) -> Unit
) {
    when (editorState) {
        is EditorState.Loaded -> ComposableFileManagerEditorContent(
            editorState,
            onClickSaveButton
        )

        is EditorState.Loading -> ComposableFileManagerInProgress(
            textId = R.string.filemanager_editor_loading_title,
            progress = editorState.progress
        )

        is EditorState.Saving -> ComposableFileManagerInProgress(
            textId = R.string.filemanager_editor_saving_title,
            progress = editorState.progress
        )

        EditorState.Saved -> return
    }
}

@Composable
private fun ComposableFileManagerEditorContent(
    loadedState: EditorState.Loaded,
    onClickSaveButton: (String) -> Unit
) {
    Column {
        var text by remember { mutableStateOf(loadedState.content) }

        ComposableEditorTopBar(loadedState.path, onClickSaveButton = {
            onClickSaveButton(text)
        })
        if (loadedState.tooLarge) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LocalPallet.current.warningColor),
                text = stringResource(R.string.filemanager_editor_warning),
                color = LocalPallet.current.textOnWarningBackground
            )
        }
        TextField(
            modifier = Modifier.fillMaxSize(),
            value = text,
            onValueChange = {
                text = it
            },
            colors = TextFieldDefaults.textFieldColors(
                cursorColor = LocalPallet.current.text100
            )
        )
    }
}

@Composable
private fun ComposableFileManagerInProgress(
    @StringRes textId: Int,
    progress: DownloadProgress
) {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Text(
            modifier = Modifier.padding(16.dp),
            text = stringResource(
                textId,
                if (progress is DownloadProgress.Fixed) {
                    progress.toProgressFloat().roundPercentToString()
                } else {
                    "~"
                }
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun ComposableFileManagerEditorScreenPreview() {
    FlipperThemeInternal {
        ComposableFileManagerEditorScreenInternal(
            EditorState.Loaded(
                path = "/ext/test",
                "Tmp",
                tooLarge = true
            ),
            onClickSaveButton = {}
        )
    }
}
