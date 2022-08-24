package com.flipperdevices.keyedit.impl.viewmodel

import android.content.Context
import android.os.Vibrator
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.api.utils.FlipperSymbolFilter
import com.flipperdevices.bridge.dao.api.delegates.FavoriteApi
import com.flipperdevices.bridge.dao.api.delegates.KeyParser
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ktx.android.vibrateCompat
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.warn
import com.flipperdevices.keyedit.impl.di.KeyEditComponent
import com.flipperdevices.keyedit.impl.model.KeyEditState
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val VIBRATOR_TIME_MS = 500L

private typealias FinishListener = (FlipperKey) -> Unit

class KeyEditViewModel(
    private val flipperKey: FlipperKey,
    context: Context,
    parsedKey: FlipperKeyParsed? = null
) : ViewModel(), LogTagProvider {
    override val TAG = "KeyEditViewModel"
    private var vibrator = ContextCompat.getSystemService(context, Vibrator::class.java)
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
        val filteredName = FlipperSymbolFilter.filterUnacceptableSymbol(newName)

        if (filteredName.length != newName.length) {
            // String contains forbidden characters
            vibrator?.vibrateCompat(VIBRATOR_TIME_MS)
        }

        lengthFilter.nameLengthFilter(filteredName) { limitedName ->
            keyEditState.update {
                if (it is KeyEditState.Editing) {
                    it.copy(name = limitedName, savingKeyActive = limitedName.isNotBlank())
                } else it
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

        val extension = flipperKey.path.nameWithExtension.substringAfterLast('.')
        val newFlipperKey = FlipperKey(
            mainFile = flipperKey.mainFile.copy(
                path = FlipperFilePath(
                    flipperKey.path.folder,
                    "${editState.name}.$extension"
                )
            ),
            flipperKeyType = flipperKey.flipperKeyType,
            notes = editState.notes,
            synchronized = false,
            deleted = flipperKey.deleted
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
