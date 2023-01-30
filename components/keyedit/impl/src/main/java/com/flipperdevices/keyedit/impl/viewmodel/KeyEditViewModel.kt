package com.flipperdevices.keyedit.impl.viewmodel

import android.content.Context
import android.os.Vibrator
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.api.utils.FlipperSymbolFilter
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.ktx.android.vibrateCompat
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.wtf
import com.flipperdevices.keyedit.api.EditableKey
import com.flipperdevices.keyedit.api.KeyEditTempStorage
import com.flipperdevices.keyedit.impl.api.EXTRA_KEY_PATH
import com.flipperdevices.keyedit.impl.model.KeyEditState
import com.flipperdevices.keyedit.impl.viewmodel.processors.EditableKeyProcessor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import tangle.inject.TangleParam
import tangle.viewmodel.VMInject

private const val VIBRATOR_TIME_MS = 500L

class KeyEditViewModel @VMInject constructor(
    context: Context,
    keyEditTempStorage: KeyEditTempStorage,
    @TangleParam(EXTRA_KEY_PATH)
    private val flipperKeyPath: FlipperKeyPath,
    private val existedKeyProcessor: EditableKeyProcessor<EditableKey.Existed>,
    private val limbKeyProcessor: EditableKeyProcessor<EditableKey.Limb>
) : ViewModel(), LogTagProvider {
    override val TAG = "KeyEditViewModel"

    private var vibrator = ContextCompat.getSystemService(context, Vibrator::class.java)
    private val lengthFilter = LengthFilter(context)

    private val keyEditState = MutableStateFlow<KeyEditState>(KeyEditState.Loading)
    private val editableKey: EditableKey = keyEditTempStorage.getEditableKey(flipperKeyPath)

    init {
        viewModelScope.launch {
            when (editableKey) {
                is EditableKey.Existed -> existedKeyProcessor.loadKey(editableKey) {
                    keyEditState.emit(it)
                }
                is EditableKey.Limb -> limbKeyProcessor.loadKey(editableKey) {
                    keyEditState.emit(it)
                }
            }
        }
    }

    fun getEditState(): StateFlow<KeyEditState> = keyEditState

    fun onNameChange(newName: String) {
        val filteredName = FlipperSymbolFilter.filterUnacceptableSymbol(newName)

        if (filteredName.length != newName.length) {
            // String contains forbidden characters
            vibrator?.vibrateCompat(VIBRATOR_TIME_MS)
        }

        lengthFilter.nameLengthFilter(filteredName) { limitedName ->
            keyEditState.update {
                if (it is KeyEditState.Editing) {
                    it.copy(name = limitedName, savingKeyActive = limitedName.isNotBlank())
                } else {
                    it
                }
            }
        }
    }

    fun onNotesChange(newNote: String) {
        lengthFilter.noteLengthFilter(newNote) { filteredName ->
            keyEditState.update {
                if (it is KeyEditState.Editing) {
                    it.copy(notes = filteredName)
                } else {
                    it
                }
            }
        }
    }

    fun onSave(onEndAction: () -> Unit) {
        val savingState = keyEditState.updateAndGet {
            if (it is KeyEditState.Editing) {
                KeyEditState.Saving(it)
            } else {
                com.flipperdevices.core.log.warn { "We try edit key without editing state" }
                return@onSave
            }
        }

        if (savingState !is KeyEditState.Saving) {
            wtf { "Saving state must be saving" }
            return
        }
        check(!savingState.editState.name.isNullOrBlank()) {
            "Save button should be inactive when name is null or blank"
        }
        viewModelScope.launch {
            try {
                when (editableKey) {
                    is EditableKey.Existed -> existedKeyProcessor.onSave(
                        editableKey,
                        savingState.editState,
                        onEndAction
                    )
                    is EditableKey.Limb -> limbKeyProcessor.onSave(
                        editableKey,
                        savingState.editState,
                        onEndAction
                    )
                }
            } catch (throwable: Throwable) {
                error(throwable) { "When save key $editableKey" }
            }
        }
    }
}
