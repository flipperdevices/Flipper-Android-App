package com.flipperdevices.nfceditor.impl.model

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Stable
data class NfcEditorState(
    val nfcEditorCardInfo: NfcEditorCardInfo? = null,
    val cardName: String? = null,
    val sectors: ImmutableList<NfcEditorSector>
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
        updatedLine = updatedLine.copy(cells = columnsList.toImmutableList())
        newLines[location.lineIndex] = updatedLine
        newSectors[location.sectorIndex] = NfcEditorSector(newLines.toImmutableList())
        return copy(
            sectors = newSectors.toImmutableList(),
            nfcEditorCardInfo = if (location.isUid(nfcEditorCardInfo)) {
                nfcEditorCardInfo?.copyWithChangedContent(
                    location,
                    content
                )
            } else {
                nfcEditorCardInfo
            }
        ).let { if (location.isUid(nfcEditorCardInfo)) it.invalidateBcc() else it }
    }

    operator fun get(location: NfcEditorCellLocation): NfcEditorCell {
        val selectableSector = when (location.field) {
            EditorField.CARD -> nfcEditorCardInfo?.fieldsAsSectors.orEmpty()
            EditorField.DATA -> sectors
        }
        val currentSector = selectableSector[location.sectorIndex]
        return currentSector.lines[location.lineIndex].cells[location.columnIndex]
    }

    private fun invalidateBcc(): NfcEditorState {
        val uidSize = nfcEditorCardInfo?.fields?.get(CardFieldInfo.UID)?.size ?: return this
        val uidCells = sectors.firstOrNull()?.lines?.firstOrNull()?.cells?.take(uidSize)
            ?: return this

        val cells = uidCells.map { if (it.content.length == 1) "${it.content}0" else it.content }
            .map { it.toIntOrNull(radix = 16) }

        for (cell in cells) {
            if (cell == null) {
                return this
            }
        }

        val bcc = cells.filterNotNull().reduce { acc, i -> acc xor i }
        var bccCellContent = bcc.toString(radix = 16).uppercase()
        if (bccCellContent.length < 2) {
            bccCellContent = "0$bccCellContent"
        }
        val location = NfcEditorCellLocation(
            field = EditorField.DATA,
            sectorIndex = 0,
            lineIndex = 0,
            columnIndex = uidSize
        )
        return copyWithChangedContent(location, bccCellContent)
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
    val cells: ImmutableList<NfcEditorCell>
)

@Stable
data class NfcEditorSector(
    val lines: ImmutableList<NfcEditorLine>
)

@Stable
data class NfcEditorCellLocation(
    val field: EditorField,
    val sectorIndex: Int,
    val lineIndex: Int,
    val columnIndex: Int
) {
    fun increment(sectors: ImmutableList<NfcEditorSector>): NfcEditorCellLocation? {
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

    fun decrement(sectors: ImmutableList<NfcEditorSector>): NfcEditorCellLocation? {
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
