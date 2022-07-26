package com.flipperdevices.nfceditor.impl.model

import androidx.annotation.IntRange
import androidx.compose.runtime.Stable

const val NFC_CELL_MAX_CURSOR_INDEX = 2L

@Stable
data class NfcEditorState(
    val lines: List<List<NfcEditorCell>>,
    val cursor: NfcEditorCursor?
) {
    fun copyWithChangedCell(
        location: NfcEditorCellLocation,
        cell: NfcEditorCell,
        newCursor: NfcEditorCursor? = cursor
    ): NfcEditorState {
        val newLines = lines.toMutableList()
        val updatedLine = newLines[location.lineIndex].toMutableList()
        updatedLine[location.columnIndex] = cell
        newLines[location.lineIndex] = updatedLine
        return NfcEditorState(
            lines = newLines,
            cursor = newCursor
        )
    }
}

@Stable
data class NfcEditorCursor(
    val location: NfcEditorCellLocation,
    @IntRange(from = 0, to = NFC_CELL_MAX_CURSOR_INDEX)
    val position: Int
) {
    constructor(lineIndex: Int, columnIndex: Int, position: Int) : this(
        NfcEditorCellLocation(
            lineIndex,
            columnIndex
        ),
        position
    )
}

@Stable
data class NfcEditorCell(
    val content: String
)

@Stable
data class NfcEditorCellLocation(
    val lineIndex: Int,
    val columnIndex: Int
)
