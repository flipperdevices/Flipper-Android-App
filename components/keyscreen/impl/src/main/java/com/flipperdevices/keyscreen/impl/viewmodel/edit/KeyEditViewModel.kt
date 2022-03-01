package com.flipperdevices.keyscreen.impl.viewmodel.edit

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.dao.api.delegates.KeyApi
import com.flipperdevices.bridge.dao.api.delegates.KeyParser
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.keyscreen.impl.di.KeyScreenComponent
import com.flipperdevices.keyscreen.impl.model.KeyEditState
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class KeyEditViewModel(
    private val keyPath: FlipperKeyPath,
    application: Application
) : AndroidViewModel(application) {
    private val nameFilter = FlipperSymbolFilter(application)

    @Inject
    lateinit var keyApi: KeyApi

    @Inject
    lateinit var parser: KeyParser

    private val keyEditState = MutableStateFlow<KeyEditState>(KeyEditState.Loading)

    init {
        ComponentHolder.component<KeyScreenComponent>().inject(this)
        viewModelScope.launch {
            val flipperKey = keyApi.getKey(keyPath)
            if (flipperKey == null) {
                keyEditState.emit(KeyEditState.Finished)
                return@launch
            }
            val parsedKey = parser.parseKey(flipperKey)
            keyEditState.emit(
                KeyEditState.Editing(
                    flipperKey.path.nameWithoutExtension,
                    flipperKey.notes,
                    parsedKey
                )
            )
        }
    }

    fun getEditState(): StateFlow<KeyEditState> = keyEditState

    fun onNameChange(newName: String) {
        nameFilter.filterUnacceptableSymbol(newName) { filteredName ->
            keyEditState.update {
                if (it is KeyEditState.Editing) {
                    it.copy(name = filteredName)
                } else it
            }
        }
    }

    fun onNotesChange(newNote: String) {
        keyEditState.update {
            if (it is KeyEditState.Editing) {
                it.copy(notes = newNote)
            } else it
        }
    }
}
