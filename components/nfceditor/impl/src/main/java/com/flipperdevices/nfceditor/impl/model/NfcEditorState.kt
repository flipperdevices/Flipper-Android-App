package com.flipperdevices.nfceditor.impl.model

import androidx.annotation.IntRange
import androidx.compose.runtime.Stable

const val NFC_CELL_MAX_CURSOR_INDEX = 2L

@Stable
data class NfcEditorState(
    val lines: List<List<NfcEditorCell>>,
    val cursor: NfcEditorCursor?
)

@Stable
data class NfcEditorCursor(
    val line: Int,
    val column: Int,
    @IntRange(from = 0, to = NFC_CELL_MAX_CURSOR_INDEX)
    val position: Int
)

@Stable
data class NfcEditorCell(
    val content: String
)
