package com.flipperdevices.nfceditor.impl.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.dao.api.delegates.KeyParser
import com.flipperdevices.bridge.dao.api.delegates.key.UpdateKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.ui.hexkeyboard.HexKey
import com.flipperdevices.core.ui.lifecycle.AndroidLifecycleViewModel
import com.flipperdevices.keyedit.api.KeyEditApi
import com.flipperdevices.keyedit.api.NotSavedFlipperKey
import com.flipperdevices.keyedit.api.toNotSavedFlipperFile
import com.flipperdevices.nfceditor.impl.R
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
import kotlinx.coroutines.withContext

class NfcEditorViewModel(
    application: Application,
    private val flipperKey: FlipperKey
) : AndroidLifecycleViewModel(application), LogTagProvider {
    override val TAG = "NfcEditorViewModel"

    @Inject
    lateinit var keyParser: KeyParser

    @Inject
    lateinit var updateKeyApi: UpdateKeyApi

    @Inject
    lateinit var synchronizationApi: SynchronizationApi

    @Inject
    lateinit var keyEditApi: KeyEditApi

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
            val newFlipperKey = NfcEditorStateProducerHelper.produceShadowFlipperKeyFromState(
                flipperKey,
                localNfcEditorState
            )
            updateKeyApi.updateKey(flipperKey, newFlipperKey)
            synchronizationApi.startSynchronization(force = true)
            router.exit()
        }
    }

    fun onSaveAs(router: Router) {
        val localNfcEditorState = getNfcEditorState().value ?: return

        viewModelScope.launch {
            val newFlipperKey = NfcEditorStateProducerHelper.produceClearFlipperKeyFromState(
                flipperKey,
                localNfcEditorState
            )
            val notSavedKey = NotSavedFlipperKey(
                mainFile = newFlipperKey.mainFile.toNotSavedFlipperFile(getApplication()),
                additionalFiles = listOf(),
                notes = newFlipperKey.notes
            )
            val saveAsTitle = withContext(Dispatchers.Main) {
                getApplication<Application>().getString(R.string.nfc_dialog_save_as_title)
            }
            router.navigateTo(keyEditApi.getScreen(notSavedKey, saveAsTitle))
        }
    }
}
