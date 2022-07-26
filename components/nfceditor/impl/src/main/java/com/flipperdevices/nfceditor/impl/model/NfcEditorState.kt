package com.flipperdevices.nfceditor.impl.model

import androidx.compose.runtime.Stable
import kotlin.math.abs

const val NFC_CELL_MAX_CURSOR_INDEX = 2L

@Stable
data class NfcEditorState(
    val sectors: List<NfcEditorSector>
) {

    fun copyWithChangedContent(location: NfcEditorCellLocation, content: String): NfcEditorState {
        val newSectors = sectors.toMutableList()
        val newLines = newSectors[location.sectorIndex].lines.toMutableList()
        var updatedLine = newLines[location.lineIndex]
        val columnsList = updatedLine.cells.toMutableList()
        columnsList[location.columnIndex] = NfcEditorCell(content)
        updatedLine = updatedLine.copy(cells = columnsList)
        newLines[location.lineIndex] = updatedLine
        newSectors[location.sectorIndex] = NfcEditorSector(newLines)
        return NfcEditorState(
            sectors = newSectors
        )
    }

    operator fun get(location: NfcEditorCellLocation): NfcEditorCell {
        return sectors[location.sectorIndex].lines[location.lineIndex].cells[location.columnIndex]
    }
}

@Stable
data class NfcEditorCell(
    val content: String
)

@Stable
data class NfcEditorLine(
    val index: Int,
    val cells: List<NfcEditorCell>
)

@Stable
data class NfcEditorSector(
    val lines: List<NfcEditorLine>
)

@Stable
data class NfcEditorCellLocation(
    val sectorIndex: Int,
    val lineIndex: Int,
    val columnIndex: Int
) {
    fun increment(sectors: List<NfcEditorSector>): NfcEditorCellLocation? {
        return if (columnIndex < sectors[sectorIndex].lines[lineIndex].cells.lastIndex) {
            NfcEditorCellLocation(
                sectorIndex = sectorIndex,
                lineIndex = lineIndex,
                columnIndex = columnIndex + 1
            )
        } else if (lineIndex < sectors[sectorIndex].lines.lastIndex) {
            NfcEditorCellLocation(
                sectorIndex = sectorIndex,
                lineIndex = lineIndex + 1,
                columnIndex = 0
            )
        } else if (sectorIndex < sectors.lastIndex) {
            NfcEditorCellLocation(
                sectorIndex = sectorIndex + 1,
                lineIndex = 0,
                columnIndex = 0
            )
        } else null
    }

    fun decrement(sectors: List<NfcEditorSector>): NfcEditorCellLocation? {
        return if (columnIndex > 0) {
            NfcEditorCellLocation(
                sectorIndex = sectorIndex,
                lineIndex = lineIndex,
                columnIndex = columnIndex - 1
            )
        } else if (lineIndex > 0) {
            val newLineIndex = lineIndex - 1
            NfcEditorCellLocation(
                sectorIndex = sectorIndex,
                lineIndex = newLineIndex,
                columnIndex = sectors[sectorIndex]
                    .lines[newLineIndex].cells.lastIndex
            )
        } else if (sectorIndex > 0) {
            val newSectorIndex = sectorIndex - 1
            NfcEditorCellLocation(
                sectorIndex = newSectorIndex,
                lineIndex = sectors[newSectorIndex].lines.lastIndex,
                columnIndex = sectors[newSectorIndex].lines.last().cells.lastIndex
            )
        } else null
    }

    fun isNear(otherLocation: NfcEditorCellLocation): Boolean {
        return otherLocation.sectorIndex == sectorIndex &&
            otherLocation.lineIndex == lineIndex &&
            abs(otherLocation.columnIndex - columnIndex) <= 1
    }
}
