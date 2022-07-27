package com.flipperdevices.nfceditor.impl.model

import androidx.annotation.IntRange
import androidx.compose.runtime.Stable

@Stable
data class NfcEditorCursor(
    val location: NfcEditorCellLocation,
    @IntRange(from = 0, to = NFC_CELL_MAX_CURSOR_INDEX)
    val position: Int
) {
    constructor(sectorIndex: Int, lineIndex: Int, columnIndex: Int, position: Int) : this(
        NfcEditorCellLocation(
            sectorIndex,
            lineIndex,
            columnIndex
        ),
        position
    )
}
