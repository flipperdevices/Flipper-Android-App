package com.flipperdevices.nfceditor.impl.viewmodel

import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed
import com.flipperdevices.nfceditor.impl.model.NfcCellType
import com.flipperdevices.nfceditor.impl.model.NfcEditorCell
import com.flipperdevices.nfceditor.impl.model.NfcEditorLine
import com.flipperdevices.nfceditor.impl.model.NfcEditorSector
import com.flipperdevices.nfceditor.impl.model.NfcEditorState

private const val EMPTY_BYTE = "$DELETE_SYMBOL$DELETE_SYMBOL"
private const val LINE_BYTES_COUNT = 16
private const val BYTES_SYMBOL_COUNT = 2
private const val MF_4K_NAME = "4K"
private const val MF_4K_LITTLE_SECTOR_COUNT = 32
private const val MF_4K_LITTLE_SECTOR_SIZE = 4
private const val MF_4K_LARGE_SECTOR_COUNT = 8
private const val MF_4K_LARGE_SECTOR_SIZE = 16
private const val MF_1K_NAME = "1K"
private const val MF_1K_SECTOR_COUNT = 16
private const val MF_1K_SECTOR_SIZE = 4

private const val LINE_1_INDEX = 0
private val LINE_1_CELL_RULES = listOf(
    IntRange(0, LINE_BYTES_COUNT - 1) to NfcCellType.UID
)
private const val LINE_4_INDEX = 3
private const val KEY_A_INDEX = 5
private const val ACCESS_BITS_INDEX = 8
private const val KEY_B_INDEX = 15
private val LINE_4_CELL_RULES = listOf(
    IntRange(0, KEY_A_INDEX) to NfcCellType.KEY_A,
    IntRange(KEY_A_INDEX + 1, ACCESS_BITS_INDEX) to NfcCellType.ACCESS_BITS,
    IntRange(ACCESS_BITS_INDEX + 2, KEY_B_INDEX) to NfcCellType.KEY_B
)

object NfcEditorStateProducerHelper {
    fun mapParsedKeyToNfcEditorState(parsedKey: FlipperKeyParsed.NFC): NfcEditorState? {
        val sectors = when (parsedKey.mifareClassicType) {
            MF_4K_NAME -> parseMifare(
                parsedKey.lines,
                MF_4K_LITTLE_SECTOR_COUNT,
                MF_4K_LITTLE_SECTOR_SIZE,
                MF_4K_LARGE_SECTOR_COUNT,
                MF_4K_LARGE_SECTOR_SIZE
            )
            MF_1K_NAME -> parseMifare(
                parsedKey.lines,
                MF_1K_SECTOR_COUNT,
                MF_1K_SECTOR_SIZE
            )
            else -> return null
        }
        return NfcEditorState(parsedKey.keyName, sectors)
    }

    private fun parseMifare(
        lines: List<Pair<Int, String>>,
        littleSectorsCount: Int,
        littleSectorsSize: Int,
        largeSectorsCount: Int = 0,
        largeSectorsSize: Int = 0
    ): List<NfcEditorSector> {
        val linesMap = lines.toMap()
        val sectors = ArrayList<NfcEditorSector>(
            littleSectorsCount + largeSectorsCount
        )
        repeat(littleSectorsCount) { sectorIndex ->
            val sectorLines = ArrayList<NfcEditorLine>(littleSectorsSize)
            repeat(littleSectorsSize) {
                val lineIndex = littleSectorsSize * sectorIndex + it
                var cells = parseLine(linesMap[lineIndex])
                if (lineIndex == LINE_1_INDEX) {
                    cells = applyColorRules(cells, LINE_1_CELL_RULES)
                } else if (lineIndex == LINE_4_INDEX) {
                    cells = applyColorRules(cells, LINE_4_CELL_RULES)
                }
                sectorLines.add(NfcEditorLine(lineIndex, cells))
            }
            sectors.add(NfcEditorSector(sectorLines))
        }
        val startIndexForLargeLines = littleSectorsCount * littleSectorsSize
        repeat(largeSectorsCount) { sectorIndex ->
            val sectorLines = ArrayList<NfcEditorLine>(largeSectorsSize)
            repeat(largeSectorsSize) {
                val lineIndex =
                    startIndexForLargeLines + largeSectorsSize * sectorIndex + it
                sectorLines.add(NfcEditorLine(lineIndex, parseLine(linesMap[lineIndex])))
            }
            sectors.add(NfcEditorSector(sectorLines))
        }

        return sectors
    }

    private fun parseLine(line: String?): List<NfcEditorCell> {
        val nonNullableLine = line ?: return buildList(LINE_BYTES_COUNT) {
            add(NfcEditorCell(EMPTY_BYTE, NfcCellType.SIMPLE))
        }

        var bytes = nonNullableLine.trim().split(" ").filterNot { it.isEmpty() }.map {
            if (it.length > BYTES_SYMBOL_COUNT) {
                it.substring(0, BYTES_SYMBOL_COUNT)
            } else if (it.length < BYTES_SYMBOL_COUNT) {
                EMPTY_BYTE.replaceRange(0, it.length, it)
            } else it
        }

        if (bytes.size < LINE_BYTES_COUNT) {
            val remainingSize = bytes.size - LINE_BYTES_COUNT
            val newBytes = ArrayList(bytes)
            repeat(remainingSize) {
                newBytes.add(EMPTY_BYTE)
            }
            bytes = newBytes
        }
        if (bytes.size > LINE_BYTES_COUNT) {
            bytes = bytes.subList(0, LINE_BYTES_COUNT - 1)
        }

        return bytes.map { NfcEditorCell(it, NfcCellType.SIMPLE) }
    }

    private fun applyColorRules(
        cells: List<NfcEditorCell>,
        cellRules: List<Pair<IntRange, NfcCellType>>
    ): List<NfcEditorCell> {
        var processedList: List<NfcEditorCell> = cells
        cellRules.forEach { cellRule ->
            processedList = processedList.mapIndexed { index, nfcEditorCell ->
                if (index in cellRule.first) {
                    nfcEditorCell.copy(cellType = cellRule.second)
                } else nfcEditorCell
            }
        }
        return processedList
    }
}
