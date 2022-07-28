package com.flipperdevices.nfceditor.impl.viewmodel

import android.view.KeyEvent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.dao.api.delegates.KeyParser
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.keyinputbus.KeyInputBus
import com.flipperdevices.core.keyinputbus.KeyInputBusListener
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.log.verbose
import com.flipperdevices.core.ui.lifecycle.LifecycleViewModel
import com.flipperdevices.nfceditor.impl.di.NfcEditorComponent
import com.flipperdevices.nfceditor.impl.model.NFC_CELL_MAX_CURSOR_INDEX
import com.flipperdevices.nfceditor.impl.model.NfcEditorCellLocation
import com.flipperdevices.nfceditor.impl.model.NfcEditorCursor
import com.flipperdevices.nfceditor.impl.model.NfcEditorState
import com.github.terrakok.cicerone.Router
import javax.inject.Inject
import javax.inject.Provider
import kotlin.math.min
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NfcEditorViewModel(
    private val flipperKey: FlipperKey
) : LifecycleViewModel(), LogTagProvider, KeyInputBusListener {
    override val TAG = "NfcEditorViewModel"

    @Inject
    lateinit var keyInputBusProvider: Provider<KeyInputBus>

    @Inject
    lateinit var keyParser: KeyParser

    @Inject
    lateinit var simpleKeyApi: SimpleKeyApi

    private var nfcEditorStateFlow = MutableStateFlow<NfcEditorState?>(
        NfcEditorState(
            sectors = emptyList()
        )
    )

    private val textUpdaterHelper = TextUpdaterHelper()

    var nfcEditorCursor by mutableStateOf<NfcEditorCursor?>(null)
        private set

    var currentActiveCell by mutableStateOf<NfcEditorCellLocation?>(null)
        private set

    init {
        ComponentHolder.component<NfcEditorComponent>().inject(this)
        keyInputBusProvider.get().subscribe(this, this)
        viewModelScope.launch(Dispatchers.Default) {
            val parsedKey = keyParser.parseKey(flipperKey)
            if (parsedKey !is FlipperKeyParsed.NFC) {
                nfcEditorStateFlow.emit(null)
                return@launch
            }
            nfcEditorStateFlow.emit(
                NfcEditorStateProducerHelper.mapParsedKeyToNfcEditorState(
                    parsedKey
                )
            )
        }
    }

    fun getNfcEditorState(): StateFlow<NfcEditorState?> = nfcEditorStateFlow

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
        val localNfcEditorState = nfcEditorStateFlow.value ?: return

        val newCursor = if (position >= NFC_CELL_MAX_CURSOR_INDEX) {
            val newLocation = oldCursor?.location?.increment(localNfcEditorState.sectors)
            if (newLocation == null) {
                NfcEditorCursor(location, min(position, NFC_CELL_MAX_CURSOR_INDEX.toInt()))
            } else NfcEditorCursor(newLocation, position = 0)
        } else NfcEditorCursor(location, position)

        val currentCellContent = localNfcEditorState[location]?.content

        val processedText = if (currentCellContent != null) {
            textUpdaterHelper.getProcessedText(
                originalText = currentCellContent,
                newText = newText,
                oldPosition = if (oldCursor?.location == location) oldCursor.position else 0,
                newPosition = position
            )
        } else null

        if (processedText != null) {
            nfcEditorStateFlow.update {
                localNfcEditorState.copyWithChangedContent(
                    location,
                    processedText
                )
            }
        }
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
        val localNfcEditorState = nfcEditorStateFlow.value ?: return

        val newCursor = cursor.location.decrement(localNfcEditorState.sectors)?.let {
            NfcEditorCursor(
                it,
                NFC_CELL_MAX_CURSOR_INDEX.toInt() - 1
            )
        }

        if (newCursor != null) {
            val location = newCursor.location
            val currentCell = localNfcEditorState[location]
            if (currentCell != null) {
                val oldText = currentCell.content
                val newText = textUpdaterHelper.getProcessedText(
                    originalText = oldText,
                    newText = oldText,
                    oldPosition = NFC_CELL_MAX_CURSOR_INDEX.toInt(),
                    newPosition = newCursor.position
                )
                nfcEditorStateFlow.update {
                    localNfcEditorState.copyWithChangedContent(
                        location,
                        content = newText
                    )
                }
            }
        }

        currentActiveCell = newCursor?.location
        nfcEditorCursor = newCursor
    }

    fun onSave(router: Router) {
        val localNfcEditorState = nfcEditorStateFlow.value ?: return

        viewModelScope.launch {
            val newFlipperKey = NfcEditorStateProducerHelper.produceFlipperKeyFromState(
                flipperKey,
                localNfcEditorState
            )
            simpleKeyApi.updateKey(flipperKey, newFlipperKey)
            router.exit()
        }
    }
}
