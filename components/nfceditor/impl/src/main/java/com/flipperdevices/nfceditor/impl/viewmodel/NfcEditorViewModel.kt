package com.flipperdevices.nfceditor.impl.viewmodel

import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.dao.api.delegates.KeyParser
import com.flipperdevices.bridge.dao.api.delegates.key.UpdateKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.ui.hexkeyboard.HexKey
import com.flipperdevices.core.ui.lifecycle.LifecycleViewModel
import com.flipperdevices.nfceditor.impl.di.NfcEditorComponent
import com.flipperdevices.nfceditor.impl.model.NfcEditorCellLocation
import com.flipperdevices.nfceditor.impl.model.NfcEditorState
import com.github.terrakok.cicerone.Router
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NfcEditorViewModel(
    private val flipperKey: FlipperKey
) : LifecycleViewModel(), LogTagProvider {
    override val TAG = "NfcEditorViewModel"

    @Inject
    lateinit var keyParser: KeyParser

    @Inject
    lateinit var updateKeyApi: UpdateKeyApi

    @Inject
    lateinit var synchronizationApi: SynchronizationApi

    private val textUpdaterHelper = TextUpdaterHelper()

    private val showOnSaveDialogState = MutableStateFlow(false)

    private var isDirty = false

    val currentActiveCell: NfcEditorCellLocation?
        get() = textUpdaterHelper.currentActiveCell

    init {
        ComponentHolder.component<NfcEditorComponent>().inject(this)
        viewModelScope.launch(Dispatchers.Default) {
            val parsedKey = keyParser.parseKey(flipperKey)
            if (parsedKey !is FlipperKeyParsed.NFC) {
                textUpdaterHelper.onFileLoad(null)
                return@launch
            }
            textUpdaterHelper.onFileLoad(
                NfcEditorStateProducerHelper.mapParsedKeyToNfcEditorState(
                    parsedKey
                )
            )
        }
    }

    fun getNfcEditorState(): StateFlow<NfcEditorState?> = textUpdaterHelper.getNfcEditorState()

    fun getShowOnSaveDialogState(): StateFlow<Boolean> = showOnSaveDialogState

    fun dismissDialog() = showOnSaveDialogState.update { false }

    fun onCellFocus(location: NfcEditorCellLocation?) {
        textUpdaterHelper.onSelectCell(location)
    }

    fun onKeyInput(hexKey: HexKey) {
        isDirty = true
        textUpdaterHelper.onKeyboardPress(hexKey)
    }

    fun onBack(router: Router) {
        if (currentActiveCell != null) {
            onCellFocus(null)
            return
        }
        if (isDirty) {
            showOnSaveDialogState.update { true }
            return
        }
        router.exit()
    }

    fun onSave(router: Router) {
        val localNfcEditorState = getNfcEditorState().value ?: return

        viewModelScope.launch {
            val newFlipperKey = NfcEditorStateProducerHelper.produceFlipperKeyFromState(
                flipperKey,
                localNfcEditorState
            )
            updateKeyApi.updateKey(flipperKey, newFlipperKey)
            synchronizationApi.startSynchronization(force = true)
            router.exit()
        }
    }
}
