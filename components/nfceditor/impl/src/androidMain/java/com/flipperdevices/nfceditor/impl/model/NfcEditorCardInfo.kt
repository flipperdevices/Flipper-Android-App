package com.flipperdevices.nfceditor.impl.model

import androidx.compose.runtime.Stable
import com.flipperdevices.core.data.PredefinedEnumMap
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

@Stable
data class NfcEditorCardInfo(
    val cardType: NfcEditorCardType,
    val fields: PredefinedEnumMap<CardFieldInfo, ImmutableList<NfcEditorCell>>
) {
    val fieldsAsSectors: ImmutableList<NfcEditorSector> by lazy {
        val lines = CardFieldInfo.values().sortedBy { it.index }.map {
            NfcEditorLine(it.index, fields[it])
        }
        persistentListOf(NfcEditorSector(lines.toImmutableList()))
    }

    fun copyWithChangedContent(
        location: NfcEditorCellLocation,
        content: String
    ): NfcEditorCardInfo {
        val fieldInfo = CardFieldInfo.byIndex(location.lineIndex) ?: return this
        val newLine = fields[fieldInfo].toMutableList()
        newLine[location.columnIndex] = newLine[location.columnIndex].copy(
            content = content
        )
        return copy(
            fields = PredefinedEnumMap(CardFieldInfo::class.java) {
                if (it == fieldInfo) newLine.toImmutableList() else fields[it]
            }
        )
    }
}

enum class NfcEditorCardType {
    MF_1K,
    MF_4K
}

enum class CardFieldInfo(val index: Int) {
    UID(index = 0),
    ATQA(index = 1),
    SAK(index = 2);

    companion object {
        fun byIndex(fieldIndex: Int): CardFieldInfo? = values().find { fieldIndex == it.index }
    }
}
