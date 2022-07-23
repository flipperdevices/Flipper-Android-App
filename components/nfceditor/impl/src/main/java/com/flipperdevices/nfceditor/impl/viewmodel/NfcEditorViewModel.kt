package com.flipperdevices.nfceditor.impl.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.nfceditor.impl.model.NFC_CELL_MAX_CURSOR_INDEX
import com.flipperdevices.nfceditor.impl.model.NfcEditorCell
import com.flipperdevices.nfceditor.impl.model.NfcEditorCursor
import com.flipperdevices.nfceditor.impl.model.NfcEditorState
import kotlin.math.min

class NfcEditorViewModel : ViewModel(), LogTagProvider {
    override val TAG = "NfcEditorViewModel"

    var nfcEditorState by mutableStateOf(
        NfcEditorState(
            listOf(listOf(NfcEditorCell("0F"), NfcEditorCell("6A"))),
            cursor = null
        )
    )
        private set

    fun onPressBack() {
        val cursor = nfcEditorState.cursor ?: return
        val newCursor = if (cursor.position > 0) {
            NfcEditorCursor(
                line = cursor.line,
                column = cursor.column,
                position = cursor.position - 1
            )
        } else if (cursor.column > 0) {
            NfcEditorCursor(
                line = cursor.line,
                column = cursor.column - 1,
                position = NFC_CELL_MAX_CURSOR_INDEX.toInt()
            )
        } else if (cursor.line > 0) {
            val newLineIndex = cursor.line - 1
            NfcEditorCursor(
                line = cursor.line - 1,
                column = nfcEditorState.lines[newLineIndex].lastIndex,
                position = NFC_CELL_MAX_CURSOR_INDEX.toInt()
            )
        } else null
        nfcEditorState = nfcEditorState.copy(cursor = newCursor)
    }

    fun onChangeSelection(line: Int, column: Int, position: Int) {
        val limitedPosition = min(position, NFC_CELL_MAX_CURSOR_INDEX.toInt())
        nfcEditorState = nfcEditorState.copy(
            cursor = NfcEditorCursor(line, column, limitedPosition)
        )
        println("On change selection: $limitedPosition")
    }
}
