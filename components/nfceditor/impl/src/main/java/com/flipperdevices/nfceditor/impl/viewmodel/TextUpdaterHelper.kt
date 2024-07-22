package com.flipperdevices.nfceditor.impl.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.flipperdevices.core.ui.hexkeyboard.HexKey
import com.flipperdevices.nfceditor.impl.model.EditorField
import com.flipperdevices.nfceditor.impl.model.NfcEditorCellLocation
import com.flipperdevices.nfceditor.impl.model.NfcEditorState
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet

const val DELETE_SYMBOL = "?"
const val DELETE_CELL = "$DELETE_SYMBOL$DELETE_SYMBOL"
const val BYTES_SYMBOL_COUNT = 2

class TextUpdaterHelper {
    private val nfcEditorStateFlow = MutableStateFlow<NfcEditorState?>(
        NfcEditorState(
            sectors = persistentListOf()
        )
    )

    private val currentActiveCellState = mutableStateOf<NfcEditorCellLocation?>(null)
    private var currentActiveCell by currentActiveCellState

    private var backupTextCurrentCell: String? = null

    fun getNfcEditorState(): StateFlow<NfcEditorState?> = nfcEditorStateFlow

    fun getActiveCellState(): State<NfcEditorCellLocation?> = currentActiveCellState

    @Synchronized
    fun onFileLoad(nfcEditorState: NfcEditorState?) {
        nfcEditorStateFlow.update { nfcEditorState }
    }

    @Synchronized
    fun onSelectCell(location: NfcEditorCellLocation?) {
        val currentNfcEditorState = nfcEditorStateFlow.value ?: return
        val activeCellLocation = currentActiveCell
        if (activeCellLocation != null &&
            currentNfcEditorState[activeCellLocation].content.length != BYTES_SYMBOL_COUNT
        ) {
            nfcEditorStateFlow.update {
                currentNfcEditorState.copyWithChangedContent(
                    activeCellLocation,
                    backupTextCurrentCell ?: DELETE_CELL
                )
            }
        }
        currentActiveCell = location
        backupTextCurrentCell = location?.let { currentNfcEditorState[location].content }
    }

    @Synchronized
    fun onKeyboardPress(
        key: HexKey
    ) {
        when (key) {
            HexKey.Ok -> onSelectCell(null)
            HexKey.Clear -> onPressClear()
            else -> onAddSymbol(key.title)
        }
    }

    private fun onPressClear() {
        val location = currentActiveCell ?: return
        var nfcEditorState = nfcEditorStateFlow.value ?: return
        nfcEditorState = nfcEditorStateFlow.updateAndGet {
            nfcEditorState.copyWithChangedContent(location, DELETE_CELL)
        } ?: return
        val newLocation = when (location.field) {
            EditorField.CARD ->
                nfcEditorState.nfcEditorCardInfo?.fieldsAsSectors
                    ?.let { location.decrement(it) }

            EditorField.DATA -> location.decrement(nfcEditorState.sectors)
        }
        onSelectCell(newLocation)
    }

    private fun onAddSymbol(symbol: Char) {
        val location = currentActiveCell ?: return
        var nfcEditorState = nfcEditorStateFlow.value ?: return
        var newText = nfcEditorState[location].content + symbol
        if (newText.length > BYTES_SYMBOL_COUNT) {
            newText = symbol.toString()
        }
        nfcEditorState = nfcEditorStateFlow.updateAndGet {
            nfcEditorState.copyWithChangedContent(location, newText)
        } ?: return
        if (newText.length == BYTES_SYMBOL_COUNT) {
            val newLocation = when (location.field) {
                EditorField.CARD ->
                    nfcEditorState.nfcEditorCardInfo?.fieldsAsSectors
                        ?.let { location.increment(it) }

                EditorField.DATA -> location.increment(nfcEditorState.sectors)
            }
            onSelectCell(newLocation)
        }
    }
}
