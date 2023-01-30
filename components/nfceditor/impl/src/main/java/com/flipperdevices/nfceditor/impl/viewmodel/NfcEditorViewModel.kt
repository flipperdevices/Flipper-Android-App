package com.flipperdevices.nfceditor.impl.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.dao.api.delegates.KeyParser
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.UpdateKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.ui.hexkeyboard.HexKey
import com.flipperdevices.core.ui.lifecycle.AndroidLifecycleViewModel
import com.flipperdevices.keyedit.api.EditableKey
import com.flipperdevices.keyedit.api.KeyEditTempStorage
import com.flipperdevices.keyedit.api.NotSavedFlipperKey
import com.flipperdevices.keyedit.api.toNotSavedFlipperFile
import com.flipperdevices.nfceditor.impl.api.EXTRA_KEY_PATH
import com.flipperdevices.nfceditor.impl.model.NfcEditorCellLocation
import com.flipperdevices.nfceditor.impl.model.NfcEditorState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tangle.inject.TangleParam
import tangle.viewmodel.VMInject

@Suppress("LongParameterList")
class NfcEditorViewModel @VMInject constructor(
    @TangleParam(EXTRA_KEY_PATH)
    private val flipperKeyPath: FlipperKeyPath,
    application: Application,
    private val keyParser: KeyParser,
    private val updateKeyApi: UpdateKeyApi,
    private val synchronizationApi: SynchronizationApi,
    private val keyEditTempStorage: KeyEditTempStorage,
    private val simpleKeyApi: SimpleKeyApi
) : AndroidLifecycleViewModel(application), LogTagProvider {
    override val TAG = "NfcEditorViewModel"

    private val textUpdaterHelper = TextUpdaterHelper()

    private val showOnSaveDialogState = MutableStateFlow(false)

    private var isDirty = false

    private val flipperKeyFlow = MutableStateFlow<FlipperKey?>(null)

    val currentActiveCell: NfcEditorCellLocation?
        get() = textUpdaterHelper.currentActiveCell

    init {
        viewModelScope.launch(Dispatchers.Default) {
            val flipperKey = requireNotNull(simpleKeyApi.getKey(flipperKeyPath)) { "Not find key by $flipperKeyPath" }
            flipperKeyFlow.emit(flipperKey)

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

    fun onProcessBack(onEndAction: () -> Unit) {
        if (currentActiveCell != null) {
            onCellFocus(null)
            return
        }
        if (isDirty) {
            showOnSaveDialogState.update { true }
            return
        }
        onEndAction()
    }

    fun onSaveProcess(onEndAction: () -> Unit) {
        val localNfcEditorState = getNfcEditorState().value ?: return

        viewModelScope.launch {
            val flipperKey = requireNotNull(flipperKeyFlow.first())

            val newFlipperKey = NfcEditorStateProducerHelper.produceShadowFlipperKeyFromState(
                flipperKey,
                localNfcEditorState
            )
            updateKeyApi.updateKey(flipperKey, newFlipperKey)
            synchronizationApi.startSynchronization(force = true)
            onEndAction()
        }
    }

    fun onSaveAs(onEndAction: (FlipperKeyPath) -> Unit) {
        val localNfcEditorState = getNfcEditorState().value ?: return

        viewModelScope.launch {
            val flipperKey = requireNotNull(flipperKeyFlow.first())

            val newFlipperKey = NfcEditorStateProducerHelper.produceClearFlipperKeyFromState(
                flipperKey,
                localNfcEditorState
            )
            val notSavedKey = NotSavedFlipperKey(
                mainFile = newFlipperKey.mainFile.toNotSavedFlipperFile(getApplication()),
                additionalFiles = listOf(),
                notes = newFlipperKey.notes
            )
            val flipperKeyPath = newFlipperKey.getKeyPath()
            keyEditTempStorage.putEditableKey(flipperKeyPath, EditableKey.Limb(notSavedKey))
            onEndAction(flipperKeyPath)
        }
    }
}
