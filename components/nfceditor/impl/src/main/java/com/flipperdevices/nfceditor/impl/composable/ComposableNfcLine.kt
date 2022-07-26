package com.flipperdevices.nfceditor.impl.composable

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.nfceditor.impl.model.NfcEditorCell
import com.flipperdevices.nfceditor.impl.model.NfcEditorCellLocation
import com.flipperdevices.nfceditor.impl.model.NfcEditorCursor

@Composable
fun ComposableNfcLine(
    lineIndex: Int,
    line: List<NfcEditorCell>,
    maxIndexSymbolCount: Int,
    scaleFactor: Float,
    cursor: NfcEditorCursor? = null,
    onFocusChanged: (NfcEditorCellLocation, isFocused: Boolean) -> Unit = { _, _ -> },
    onValueChanged: (NfcEditorCellLocation, TextFieldValue) -> Unit = { _, _ -> }
) {
    Row {
        Text(
            modifier = Modifier.width((scaleFactor * WIDTH_LINE_INDEX_DP * maxIndexSymbolCount).dp),
            text = lineIndex.toString(),
            textAlign = TextAlign.End,
            color = LocalPallet.current.text16,
            fontSize = (scaleFactor * FONT_SIZE_SP).sp
        )

        line.forEachIndexed { columnIndex, cell ->
            var focusRequester: FocusRequester? = null
            var textSelection: TextRange? = null
            val cellLocation = NfcEditorCellLocation(lineIndex, columnIndex)
            if (cursor != null &&
                cursor.location == cellLocation
            ) {
                textSelection = TextRange(cursor.position)
                focusRequester = remember { FocusRequester() }
                LaunchedEffect(cursor.location) {
                    focusRequester.requestFocus()
                }
            }
            ComposableNfcCell(
                focusRequester,
                cell,
                scaleFactor,
                textSelection,
                onFocusChanged = { onFocusChanged(cellLocation, it) },
                onValueChanged = { onValueChanged(cellLocation, it) }
            )
        }
    }
}
