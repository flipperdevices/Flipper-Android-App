package com.flipperdevices.keyedit.impl.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.dao.api.delegates.FavoriteApi
import com.flipperdevices.bridge.dao.api.delegates.KeyApi
import com.flipperdevices.bridge.dao.api.delegates.KeyParser
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.warn
import com.flipperdevices.keyedit.impl.di.KeyEditComponent
import com.flipperdevices.keyedit.impl.model.KeyEditState
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class KeyEditViewModel(
    private val flipperKey: FlipperKey,
    context: Context,
    parsedKey: FlipperKeyParsed? = null
) : ViewModel(), LogTagProvider {
    override val TAG = "KeyEditViewModel"
    private val nameFilter = FlipperSymbolFilter(context)

    @Inject
    lateinit var keyApi: KeyApi

    @Inject
    lateinit var favoriteApi: FavoriteApi

    @Inject
    lateinit var parser: KeyParser

    private val keyEditState = MutableStateFlow<KeyEditState>(KeyEditState.Loading)

    init {
        ComponentHolder.component<KeyEditComponent>().inject(this)
        setUpInitState(parsedKey)
    }

    fun getEditState(): StateFlow<KeyEditState> = keyEditState

    fun onNameChange(newName: String) {
        nameFilter.filterUnacceptableSymbol(newName) { filteredName ->
            keyEditState.update {
                if (it is KeyEditState.Editing) {
                    it.copy(name = filteredName, savingKeyActive = filteredName.isNotBlank())
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

    fun cancel() {
        keyEditState.update { KeyEditState.Finished(flipperKey) }
    }

    fun onSave() {
        val editState = keyEditState.value
        if (editState !is KeyEditState.Editing) {
            warn { "We try edit key without editing state" }
            return
        }
        val newState = KeyEditState.Saving(
            editState.name,
            editState.notes,
            editState.parsedKey
        )
        val isStateSaved = keyEditState.compareAndSet(editState, newState)
        if (!isStateSaved) {
            warn { "State save failed" }
            onSave()
            return
        }

        viewModelScope.launch {
            @Suppress("TooGenericExceptionCaught")
            try {
                onSaveInternal(newState.name, newState.notes)
            } catch (e: Exception) {
                error(e) { "Error while we try save exception" }
                keyEditState.update { editState }
            }
        }
    }

    private suspend fun onSaveInternal(newName: String?, newNote: String?) {
        check(!newName.isNullOrBlank()) {
            "Save button should be inactive when name is null or blank"
        }
        val oldPath = flipperKey.path
        val extension = oldPath.name.substringAfterLast('.')
        val newPath = FlipperKeyPath(oldPath.folder, "$newName.$extension")

        if (oldPath == newPath && !newNote.isNullOrBlank()) {
            keyApi.updateNote(oldPath, newNote)
            keyEditState.emit(
                KeyEditState.Finished(
                    flipperKey.copy(notes = newNote)
                )
            )
            return
        }

        val newFlipperKey = FlipperKey(
            path = newPath,
            keyContent = flipperKey.keyContent,
            notes = newNote
        )
        val isFavorite = favoriteApi.isFavorite(oldPath)
        if (isFavorite) {
            // Delete key from favorite, because we can't delete it
            favoriteApi.setFavorite(oldPath, false)
        }
        keyApi.markDeleted(oldPath)
        keyApi.insertKey(newFlipperKey)
        if (isFavorite) {
            favoriteApi.setFavorite(newFlipperKey.path, true)
        }
        keyEditState.emit(KeyEditState.Finished(newFlipperKey))
    }

    private fun setUpInitState(parsedKey: FlipperKeyParsed?) {
        if (parsedKey != null) {
            keyEditState.update {
                KeyEditState.Editing(
                    flipperKey.path.nameWithoutExtension,
                    flipperKey.notes,
                    parsedKey,
                    flipperKey.path.nameWithoutExtension.isNotBlank()
                )
            }
            return
        }

        viewModelScope.launch {
            val parsedKeyLoaded = parser.parseKey(flipperKey)
            keyEditState.emit(
                KeyEditState.Editing(
                    flipperKey.path.nameWithoutExtension,
                    flipperKey.notes,
                    parsedKeyLoaded,
                    flipperKey.path.nameWithoutExtension.isNotBlank()
                )
            )
        }
    }
}
