package com.flipperdevices.filemanager.editor.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.flipperdevices.core.ui.ktx.OrangeAppBar
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.filemanager.editor.model.EditorEncodingEnum
import okio.Path

@Composable
internal fun EditorAppBar(
    path: Path,
    onSaveClick: () -> Unit,
    onSaveAsClick: () -> Unit,
    onBack: () -> Unit,
    editorEncodingEnum: EditorEncodingEnum?,
    onEditorTabChange: (EditorEncodingEnum) -> Unit,
    canSave: Boolean,
    modifier: Modifier = Modifier
) {
    OrangeAppBar(
        title = path.name,
        onBack = onBack,
        modifier = modifier,
        endBlock = {
            Row(
                modifier = Modifier.background(LocalPallet.current.accent),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedVisibility(
                    modifier = Modifier.weight(1f, false),
                    visible = editorEncodingEnum != null
                ) {
                    editorEncodingEnum?.let {
                        EditorTabSwitch(
                            editorEncodingEnum = editorEncodingEnum,
                            onEditorTabChange = onEditorTabChange
                        )
                    }
                }
                AnimatedVisibility(
                    modifier = Modifier.weight(1f, false),
                    visible = canSave
                ) {
                    EditorDropdown(
                        onSaveAsClick = onSaveAsClick,
                        onSaveClick = onSaveClick
                    )
                }
            }
        }
    )
}
