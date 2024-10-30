package com.flipperdevices.filemanager.editor.composable

import android.annotation.SuppressLint
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.filemanager.editor.model.EditorEncodingEnum
import okio.Path.Companion.toPath

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Preview
@Composable
private fun EditorAppBarPreview() {
    FlipperThemeInternal {
        Scaffold(
            topBar = {
                EditorAppBar(
                    path = "file.txt".toPath(),
                    onBack = {},
                    onSaveClick = {},
                    onSaveAsClick = {},
                    editorEncodingEnum = EditorEncodingEnum.TEXT,
                    onEditorTabChange = {},
                    canSave = true
                )
            }
        ) { }
    }
}
