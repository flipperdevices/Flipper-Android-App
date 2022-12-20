package com.flipperdevices.nfceditor.impl.model

import androidx.compose.runtime.Stable

@Stable
data class NfcEditorState(
    val nfcEditorCardInfo: NfcEditorCardInfo? = null,
    val cardName: String? = null,
    val sectors: List<NfcEditorSector>
) {
    fun copyWithChangedContent(location: NfcEditorCellLocation, content: String): NfcEditorState {
        if (location.field == EditorField.CARD) {
            return copy(
                nfcEditorCardInfo = nfcEditorCardInfo?.copyWithChangedContent(
                    location,
                    content
                )
            )
        }
        val newSectors = sectors.toMutableList()
        val currentSector = newSectors.getOrNull(location.sectorIndex) ?: return this
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
        return copy(
            sectors = newSectors,
            nfcEditorCardInfo = if (location.isUid(nfcEditorCardInfo)) {
                nfcEditorCardInfo?.copyWithChangedContent(
                    location,
                    content
                )
            } else {
                nfcEditorCardInfo
            }
        )
    }

    operator fun get(location: NfcEditorCellLocation): NfcEditorCell {
        val selectableSector = when (location.field) {
            EditorField.CARD -> nfcEditorCardInfo?.fieldsAsSectors ?: emptyList()
            EditorField.DATA -> sectors
        }
        val currentSector = selectableSector[location.sectorIndex]
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
    KEY_B,

    // for display color on card
    ON_CARD,
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
    val field: EditorField,
    val sectorIndex: Int,
    val lineIndex: Int,
    val columnIndex: Int
) {
    fun increment(sectors: List<NfcEditorSector>): NfcEditorCellLocation? {
        val currentSector = sectors[sectorIndex]
        return if (columnIndex < currentSector.lines[lineIndex].cells.lastIndex) {
            NfcEditorCellLocation(
                field = field,
                sectorIndex = sectorIndex,
                lineIndex = lineIndex,
                columnIndex = columnIndex + 1
            )
        } else if (lineIndex < currentSector.lines.lastIndex) {
            NfcEditorCellLocation(
                field = field,
                sectorIndex = sectorIndex,
                lineIndex = lineIndex + 1,
                columnIndex = 0
            )
        } else if (sectorIndex < sectors.lastIndex) {
            NfcEditorCellLocation(
                field = field,
                sectorIndex = sectorIndex + 1,
                lineIndex = 0,
                columnIndex = 0
            )
        } else {
            null
        }
    }

    fun decrement(sectors: List<NfcEditorSector>): NfcEditorCellLocation? {
        val currentSector = sectors[sectorIndex]

        if (columnIndex > 0) {
            return NfcEditorCellLocation(
                field = field,
                sectorIndex = sectorIndex,
                lineIndex = lineIndex,
                columnIndex = columnIndex - 1
            )
        } else if (lineIndex > 0) {
            val newLineIndex = lineIndex - 1
            return NfcEditorCellLocation(
                field = field,
                sectorIndex = sectorIndex,
                lineIndex = newLineIndex,
                columnIndex = currentSector
                    .lines[newLineIndex].cells.lastIndex
            )
        } else if (sectorIndex > 0) {
            val newSectorIndex = sectorIndex - 1
            val newSector = sectors[newSectorIndex]

            return NfcEditorCellLocation(
                field = field,
                sectorIndex = newSectorIndex,
                lineIndex = newSector.lines.lastIndex,
                columnIndex = newSector.lines.last().cells.lastIndex
            )
        } else {
            return null
        }
    }

    fun isUid(nfcEditorCardInfo: NfcEditorCardInfo?): Boolean {
        if (nfcEditorCardInfo == null) {
            return false
        }
        return sectorIndex == 0 &&
            lineIndex == 0 &&
            columnIndex < nfcEditorCardInfo.fields[CardFieldInfo.UID].size
    }
}

enum class EditorField {
    CARD,
    DATA
}
