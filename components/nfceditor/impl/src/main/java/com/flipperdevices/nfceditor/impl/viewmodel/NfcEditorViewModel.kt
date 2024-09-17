package com.flipperdevices.nfceditor.impl.viewmodel

import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.UpdateKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.ui.hexkeyboard.HexKey
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.keyedit.api.NotSavedFlipperKey
import com.flipperdevices.keyedit.api.NotSavedFlipperKeyApi
import com.flipperdevices.keyparser.api.KeyParser
import com.flipperdevices.keyparser.api.model.FlipperKeyParsed
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.SimpleEvent
import com.flipperdevices.nfceditor.impl.model.NfcEditorCellLocation
import com.flipperdevices.nfceditor.impl.model.NfcEditorState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Suppress("LongParameterList")
class NfcEditorViewModel @AssistedInject constructor(
    @Assisted private val flipperKeyPath: FlipperKeyPath,
    private val keyParser: KeyParser,
    private val updateKeyApi: UpdateKeyApi,
    private val synchronizationApi: SynchronizationApi,
    private val simpleKeyApi: SimpleKeyApi,
    private val metricApi: MetricApi,
    private val notSavedFlipperKeyApi: NotSavedFlipperKeyApi
) : DecomposeViewModel(), LogTagProvider {
    override val TAG = "NfcEditorViewModel"

    private val textUpdaterHelper = TextUpdaterHelper()

    private val showOnSaveDialogState = MutableStateFlow(false)

    private var isDirty = false

    private val flipperKeyFlow = MutableStateFlow<FlipperKey?>(null)

    fun getCurrentActiveCellState() = textUpdaterHelper.getActiveCellState()

    init {
        viewModelScope.launch {
            val flipperKey =
                requireNotNull(simpleKeyApi.getKey(flipperKeyPath)) { "Not find key by $flipperKeyPath" }
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
        if (textUpdaterHelper.getActiveCellState().value != null) {
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
            metricApi.reportSimpleEvent(SimpleEvent.SAVE_DUMP)
            onEndAction()
        }
    }

    fun onSaveAs(onEndAction: (NotSavedFlipperKey) -> Unit) {
        val localNfcEditorState = getNfcEditorState().value ?: return

        viewModelScope.launch {
            val flipperKey = requireNotNull(flipperKeyFlow.first())

            val newFlipperKey = NfcEditorStateProducerHelper.produceClearFlipperKeyFromState(
                flipperKey,
                localNfcEditorState
            )
            val notSavedKey = NotSavedFlipperKey(
                mainFile = notSavedFlipperKeyApi.toNotSavedFlipperFile(newFlipperKey.mainFile),
                additionalFiles = listOf(),
                notes = newFlipperKey.notes
            )
            metricApi.reportSimpleEvent(SimpleEvent.SAVE_DUMP)
            onEndAction(notSavedKey)
        }
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(flipperKeyPath: FlipperKeyPath): NfcEditorViewModel
    }
}
