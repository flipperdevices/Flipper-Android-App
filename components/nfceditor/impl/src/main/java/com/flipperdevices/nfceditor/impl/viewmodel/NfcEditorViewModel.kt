package com.flipperdevices.nfceditor.impl.viewmodel

import android.view.KeyEvent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.keyinputbus.KeyInputBus
import com.flipperdevices.core.keyinputbus.KeyInputBusListener
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.ui.lifecycle.LifecycleViewModel
import com.flipperdevices.nfceditor.impl.di.NfcEditorComponent
import com.flipperdevices.nfceditor.impl.model.NFC_CELL_MAX_CURSOR_INDEX
import com.flipperdevices.nfceditor.impl.model.NfcEditorCell
import com.flipperdevices.nfceditor.impl.model.NfcEditorCellLocation
import com.flipperdevices.nfceditor.impl.model.NfcEditorCursor
import com.flipperdevices.nfceditor.impl.model.NfcEditorState
import javax.inject.Inject
import javax.inject.Provider
import kotlin.math.min

class NfcEditorViewModel : LifecycleViewModel(), LogTagProvider, KeyInputBusListener {
    override val TAG = "NfcEditorViewModel"

    @Inject
    lateinit var keyInputBusProvider: Provider<KeyInputBus>

    init {
        ComponentHolder.component<NfcEditorComponent>().inject(this)
        keyInputBusProvider.get().subscribe(this, this)
    }

    var nfcEditorState by mutableStateOf(
        NfcEditorState(
            listOf(listOf(NfcEditorCell("0F"), NfcEditorCell("6A"))),
            cursor = null
        )
    )
        private set

    var currentActiveCell by mutableStateOf<NfcEditorCellLocation?>(null)

    fun onChangeSelection(location: NfcEditorCellLocation, position: Int) {
        val newCursor = if (position >= NFC_CELL_MAX_CURSOR_INDEX) {
            if (location.columnIndex < nfcEditorState.lines[location.lineIndex].lastIndex) {
                // If it is not last column:
                NfcEditorCursor(
                    lineIndex = location.lineIndex,
                    columnIndex = location.columnIndex + 1,
                    position = 0
                )
            } else if (location.lineIndex < nfcEditorState.lines.lastIndex) {
                // If it is not last line:
                NfcEditorCursor(
                    lineIndex = location.lineIndex + 1,
                    columnIndex = 0,
                    position = 0
                )
            } else NfcEditorCursor(location, min(position, NFC_CELL_MAX_CURSOR_INDEX.toInt()))
        } else NfcEditorCursor(location, position)
        nfcEditorState = nfcEditorState.copy(
            cursor = newCursor
        )
        println("On change selection. Input $location and $position. Output: $newCursor")
    }

    override fun onKeyEvent(keyEvent: KeyEvent) {
        if (keyEvent.keyCode == KeyEvent.KEYCODE_DEL &&
            keyEvent.action == KeyEvent.ACTION_UP &&
            currentActiveCell != null
        ) {
            info { "On key event $keyEvent" }
            onPressBack()
        }
    }

    private fun onPressBack() {
        val cursor = nfcEditorState.cursor ?: return

        // Execute only if current selector on zero
        if (cursor.position > 0) {
            return
        }

        val newCursor = if (cursor.location.columnIndex > 0) {
            NfcEditorCursor(
                lineIndex = cursor.location.lineIndex,
                columnIndex = cursor.location.columnIndex - 1,
                position = NFC_CELL_MAX_CURSOR_INDEX.toInt() - 1
            )
        } else if (cursor.location.lineIndex > 0) {
            val newLineIndex = cursor.location.lineIndex - 1
            NfcEditorCursor(
                lineIndex = newLineIndex,
                columnIndex = nfcEditorState.lines[newLineIndex].lastIndex,
                position = NFC_CELL_MAX_CURSOR_INDEX.toInt() - 1
            )
        } else null
        nfcEditorState = nfcEditorState.copy(cursor = newCursor)
    }
}
