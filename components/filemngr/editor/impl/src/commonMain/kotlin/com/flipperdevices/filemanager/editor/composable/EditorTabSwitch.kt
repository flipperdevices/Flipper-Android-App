package com.flipperdevices.filemanager.editor.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.tabswitch.ComposableTabSwitch
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.filemanager.editor.model.EditorEncodingEnum

@Composable
internal fun EditorTabSwitch(
    editorEncodingEnum: EditorEncodingEnum,
    onEditorTabChange: (EditorEncodingEnum) -> Unit,
    modifier: Modifier = Modifier
) {
    ComposableTabSwitch(
        modifier = modifier,
        typeTab = EditorEncodingEnum::class.java,
        currentTab = editorEncodingEnum,
    ) { tab ->
        Text(
            modifier = Modifier
                .clickable { onEditorTabChange.invoke(tab) }
                .padding(vertical = 8.dp),
            text = when (tab) {
                EditorEncodingEnum.TEXT -> "TXT"
                EditorEncodingEnum.HEX -> "HEX"
            },
            textAlign = TextAlign.Center,
            color = LocalPalletV2.current.text.label.primary,
            style = LocalTypography.current.subtitleB12
        )
    }
}
