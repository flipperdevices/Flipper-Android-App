package com.flipperdevices.nfceditor.impl.model

import androidx.compose.runtime.Stable
import kotlin.math.abs

const val NFC_CELL_MAX_CURSOR_INDEX = 2L

@Stable
data class NfcEditorState(
    val nfcEditorCardInfo: NfcEditorCardInfo? = null,
    val cardName: String? = null,
    val sectors: List<NfcEditorSector?>
) {

    fun copyWithChangedContent(location: NfcEditorCellLocation, content: String): NfcEditorState {
        val newSectors = sectors.toMutableList()
        val currentSector = newSectors[location.sectorIndex] ?: return this
        val newLines = currentSector.lines.toMutableList()
        var updatedLine = newLines[location.lineIndex]
        val columnsList = updatedLine.cells.toMutableList()
        columnsList[location.columnIndex] = NfcEditorCell(
            content,
            columnsList[location.columnIndex].cellType
        )
        updatedLine = updatedLine.copy(cells = columnsList)
        newLines[location.lineIndex] = updatedLine
        newSectors[location.sectorIndex] = NfcEditorSector(newLines)
        return NfcEditorState(
            sectors = newSectors
        )
    }

    operator fun get(location: NfcEditorCellLocation): NfcEditorCell? {
        val currentSector = sectors[location.sectorIndex] ?: return null
        return currentSector.lines[location.lineIndex].cells[location.columnIndex]
    }
}

@Stable
data class NfcEditorCell(
    val content: String,
    val cellType: NfcCellType
)

enum class NfcCellType {
    SIMPLE,
    UID,
    KEY_A,
    ACCESS_BITS,
    KEY_B
}

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
    fun increment(sectors: List<NfcEditorSector?>): NfcEditorCellLocation? {
        val currentSector = sectors[sectorIndex] ?: return null
        return if (columnIndex < currentSector.lines[lineIndex].cells.lastIndex) {
            NfcEditorCellLocation(
                sectorIndex = sectorIndex,
                lineIndex = lineIndex,
                columnIndex = columnIndex + 1
            )
        } else if (lineIndex < currentSector.lines.lastIndex) {
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

    fun decrement(sectors: List<NfcEditorSector?>): NfcEditorCellLocation? {
        val currentSector = sectors[sectorIndex] ?: return null

        if (columnIndex > 0) {
            return NfcEditorCellLocation(
                sectorIndex = sectorIndex,
                lineIndex = lineIndex,
                columnIndex = columnIndex - 1
            )
        } else if (lineIndex > 0) {
            val newLineIndex = lineIndex - 1
            return NfcEditorCellLocation(
                sectorIndex = sectorIndex,
                lineIndex = newLineIndex,
                columnIndex = currentSector
                    .lines[newLineIndex].cells.lastIndex
            )
        } else if (sectorIndex > 0) {
            var newSectorIndex = sectorIndex - 1
            while (newSectorIndex >= 0 && sectors[newSectorIndex] == null) {
                newSectorIndex--
            }
            if (newSectorIndex < 0) {
                return null
            }
            val newSector = sectors[newSectorIndex] ?: return null

            return NfcEditorCellLocation(
                sectorIndex = newSectorIndex,
                lineIndex = newSector.lines.lastIndex,
                columnIndex = newSector.lines.last().cells.lastIndex
            )
        } else return null
    }

    fun isNear(otherLocation: NfcEditorCellLocation): Boolean {
        return otherLocation.sectorIndex == sectorIndex &&
            otherLocation.lineIndex == lineIndex &&
            abs(otherLocation.columnIndex - columnIndex) <= 1
    }
}
