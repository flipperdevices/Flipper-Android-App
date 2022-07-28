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
import com.flipperdevices.core.log.verbose
import com.flipperdevices.core.ui.lifecycle.LifecycleViewModel
import com.flipperdevices.nfceditor.impl.di.NfcEditorComponent
import com.flipperdevices.nfceditor.impl.model.NFC_CELL_MAX_CURSOR_INDEX
import com.flipperdevices.nfceditor.impl.model.NfcEditorCell
import com.flipperdevices.nfceditor.impl.model.NfcEditorCellLocation
import com.flipperdevices.nfceditor.impl.model.NfcEditorCursor
import com.flipperdevices.nfceditor.impl.model.NfcEditorLine
import com.flipperdevices.nfceditor.impl.model.NfcEditorSector
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

    private val textUpdaterHelper = TextUpdaterHelper()

    @Suppress("MagicNumber")
    var nfcEditorState by mutableStateOf(
        NfcEditorState(
            List(size = (256 / 4)) { sectorIndex ->
                NfcEditorSector(
                    List(4) { lineIndex ->
                        NfcEditorLine(
                            sectorIndex * 4 + lineIndex,
                            cells = "B6 69 03 36 8A 98 02 00 64 8F 76 14 51 10 37 11".split(" ")
                                .map { NfcEditorCell(it) }
                        )
                    }
                )
            }
        )
    )
        private set

    var nfcEditorCursor by mutableStateOf<NfcEditorCursor?>(null)
        private set

    var currentActiveCell by mutableStateOf<NfcEditorCellLocation?>(null)
        private set

    fun onCellFocus(location: NfcEditorCellLocation, isFocused: Boolean) {
        if (!isFocused) {
            if (currentActiveCell == location) {
                currentActiveCell = null
            }
            return
        }

        currentActiveCell = location
        if (nfcEditorCursor?.location != location) {
            nfcEditorCursor = NfcEditorCursor(location, position = 0)
        }
    }

    fun onChangeText(newText: String, location: NfcEditorCellLocation, position: Int) {
        val oldCursor = nfcEditorCursor
        if (oldCursor != null &&
            location == oldCursor.location &&
            position == oldCursor.position
        ) {
            return
        }

        if (currentActiveCell != location) {
            return
        }

        val newCursor = if (position >= NFC_CELL_MAX_CURSOR_INDEX) {
            val newLocation = oldCursor?.location?.increment(nfcEditorState.sectors)
            if (newLocation == null) {
                NfcEditorCursor(location, min(position, NFC_CELL_MAX_CURSOR_INDEX.toInt()))
            } else NfcEditorCursor(newLocation, position = 0)
        } else NfcEditorCursor(location, position)

        val processedText = textUpdaterHelper.getProcessedText(
            originalText = nfcEditorState[location].content,
            newText = newText,
            oldPosition = if (oldCursor?.location == location) oldCursor.position else 0,
            newPosition = position
        )

        nfcEditorState = nfcEditorState.copyWithChangedContent(
            location,
            processedText
        )
        nfcEditorCursor = newCursor
        verbose {
            "On change selection. Input $location and $position. " +
                "Output: $newCursor with $processedText"
        }
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
        val cursor = nfcEditorCursor ?: return

        // Execute only if current selector on zero
        if (cursor.position > 0) {
            return
        }

        val newCursor = cursor.location.decrement(nfcEditorState.sectors)?.let {
            NfcEditorCursor(
                it,
                NFC_CELL_MAX_CURSOR_INDEX.toInt() - 1
            )
        }

        if (newCursor != null) {
            val location = newCursor.location
            val currentCell = nfcEditorState[location]
            val oldText = currentCell.content
            val newText = textUpdaterHelper.getProcessedText(
                originalText = oldText,
                newText = oldText,
                oldPosition = NFC_CELL_MAX_CURSOR_INDEX.toInt(),
                newPosition = newCursor.position
            )
            nfcEditorState = nfcEditorState.copyWithChangedContent(
                location,
                content = newText
            )
        }

        currentActiveCell = newCursor?.location
        nfcEditorCursor = newCursor
    }
}
