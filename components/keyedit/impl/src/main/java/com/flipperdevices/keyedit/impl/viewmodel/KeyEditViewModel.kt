package com.flipperdevices.keyedit.impl.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.dao.api.delegates.FavoriteApi
import com.flipperdevices.bridge.dao.api.delegates.KeyParser
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.warn
import com.flipperdevices.keyedit.impl.di.KeyEditComponent
import com.flipperdevices.keyedit.impl.model.KeyEditState
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private typealias FinishListener = (FlipperKey) -> Unit

class KeyEditViewModel(
    private val flipperKey: FlipperKey,
    context: Context,
    parsedKey: FlipperKeyParsed? = null
) : ViewModel(), LogTagProvider {
    override val TAG = "KeyEditViewModel"
    private val nameFilter = FlipperSymbolFilter(context)
    private val lengthFilter = LengthFilter(context)

    @Inject
    lateinit var favoriteApi: FavoriteApi

    @Inject
    lateinit var parser: KeyParser

    private val keyEditState = MutableStateFlow<KeyEditState>(KeyEditState.Loading)
    private var finishListener: FinishListener? = null

    init {
        ComponentHolder.component<KeyEditComponent>().inject(this)
        setUpInitState(parsedKey)
    }

    fun getEditState(): StateFlow<KeyEditState> = keyEditState

    fun onNameChange(newName: String) {
        nameFilter.filterUnacceptableSymbol(newName) {
            lengthFilter.nameLengthFilter(it) { filteredName ->
                keyEditState.update {
                    if (it is KeyEditState.Editing) {
                        it.copy(name = filteredName, savingKeyActive = filteredName.isNotBlank())
                    } else it
                }
            }
        }
    }

    fun onNotesChange(newNote: String) {
        lengthFilter.noteLengthFilter(newNote) { filteredName ->
            keyEditState.update {
                if (it is KeyEditState.Editing) {
                    it.copy(notes = filteredName)
                } else it
            }
        }
    }

    fun subscribeOnFinish(finishListener: FinishListener) {
        this.finishListener = finishListener
    }

    fun cancel() {
        finishListener?.invoke(flipperKey)
    }

    fun onSave() {
        val editState = keyEditState.value
        if (editState !is KeyEditState.Editing) {
            warn { "We try edit key without editing state" }
            return
        }
        check(!editState.name.isNullOrBlank()) {
            "Save button should be inactive when name is null or blank"
        }

        val extension = flipperKey.path.name.substringAfterLast('.')
        val newFlipperKey = FlipperKey(
            FlipperKeyPath(
                flipperKey.path.folder,
                "${editState.name}.$extension",
                flipperKey.path.deleted
            ),
            flipperKey.keyContent,
            editState.notes,
            synchronized = false
        )
        finishListener?.invoke(newFlipperKey)
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
