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
    sectorIndex: Int,
    lineIndexInSector: Int,
    visibleIndex: Int,
    line: List<NfcEditorCell>,
    maxIndexSymbolCount: Int,
    scaleFactor: Float,
    cursor: NfcEditorCursor? = null,
    onFocusChanged: ((NfcEditorCellLocation, isFocused: Boolean) -> Unit)? = null,
    onValueChanged: ((NfcEditorCellLocation, TextFieldValue) -> Unit)? = null
) {
    Row {
        Text(
            modifier = Modifier.width((scaleFactor * WIDTH_LINE_INDEX_DP * maxIndexSymbolCount).dp),
            text = visibleIndex.toString(),
            textAlign = TextAlign.End,
            color = LocalPallet.current.text16,
            fontSize = (scaleFactor * FONT_SIZE_SP).sp,
            maxLines = 1
        )

        line.forEachIndexed { columnIndex, cell ->
            var focusRequester: FocusRequester? = null
            var textSelection: TextRange? = null
            val cellLocation = remember(sectorIndex, lineIndexInSector, columnIndex) {
                NfcEditorCellLocation(sectorIndex, lineIndexInSector, columnIndex)
            }
            if (cursor != null &&
                cursor.location == cellLocation
            ) {
                textSelection = TextRange(cursor.position)
                focusRequester = remember { FocusRequester() }
                LaunchedEffect(cursor.location) {
                    focusRequester.requestFocus()
                }
            }
            val isEditable = remember(cellLocation, cursor) {
                cursor != null &&
                    cellLocation.isNear(cursor.location)
            }
            ComposableNfcCell(
                focusRequester,
                cell,
                scaleFactor,
                textSelection,
                isEditable = isEditable,
                onFocusChanged = { onFocusChanged?.invoke(cellLocation, it) },
                onValueChanged = { onValueChanged?.invoke(cellLocation, it) }
            )
        }
    }
}
