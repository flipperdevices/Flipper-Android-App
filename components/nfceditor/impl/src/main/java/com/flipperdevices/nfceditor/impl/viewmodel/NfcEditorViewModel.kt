package com.flipperdevices.nfceditor.impl.viewmodel

import android.view.KeyEvent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.keyinputbus.KeyInputBus
import com.flipperdevices.core.keyinputbus.KeyInputBusListener
import com.flipperdevices.core.log.LogTagProvider
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
        val limitedPosition = min(position, NFC_CELL_MAX_CURSOR_INDEX.toInt())
        nfcEditorState = nfcEditorState.copy(
            cursor = NfcEditorCursor(location, limitedPosition)
        )
        println("On change selection: $limitedPosition")
    }

    override fun onKeyEvent(keyEvent: KeyEvent) {
        if (keyEvent.keyCode == KeyEvent.KEYCODE_DEL &&
            keyEvent.action == KeyEvent.ACTION_UP &&
            currentActiveCell != null
        ) {
            onPressBack()
        }
    }

    private fun onPressBack() {
        val cursor = nfcEditorState.cursor ?: return

        val newCursor = if (cursor.position > 0) {
            NfcEditorCursor(
                cursor.location,
                position = cursor.position - 1
            )
        } else if (cursor.location.column > 0) {
            NfcEditorCursor(
                NfcEditorCellLocation(
                    line = cursor.location.line,
                    column = cursor.location.column - 1
                ),
                position = NFC_CELL_MAX_CURSOR_INDEX.toInt()
            )
        } else if (cursor.location.line > 0) {
            val newLineIndex = cursor.location.line - 1
            NfcEditorCursor(
                NfcEditorCellLocation(
                    line = newLineIndex,
                    column = nfcEditorState.lines[newLineIndex].lastIndex
                ),
                position = NFC_CELL_MAX_CURSOR_INDEX.toInt()
            )
        } else null
        nfcEditorState = nfcEditorState.copy(cursor = newCursor)
    }
}
