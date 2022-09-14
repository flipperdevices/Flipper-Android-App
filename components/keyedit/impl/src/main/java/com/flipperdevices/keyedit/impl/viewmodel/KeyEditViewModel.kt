package com.flipperdevices.keyedit.impl.viewmodel

import android.content.Context
import android.os.Vibrator
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.api.utils.FlipperSymbolFilter
import com.flipperdevices.bridge.dao.api.delegates.FavoriteApi
import com.flipperdevices.bridge.dao.api.delegates.KeyParser
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.UpdateKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ktx.android.vibrateCompat
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.warn
import com.flipperdevices.core.log.wtf
import com.flipperdevices.keyedit.impl.di.KeyEditComponent
import com.flipperdevices.keyedit.impl.model.KeyEditState
import com.github.terrakok.cicerone.Router
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch

private const val VIBRATOR_TIME_MS = 500L

private typealias FinishListener = (FlipperKey) -> Unit

class KeyEditViewModel(
    private val keyPath: FlipperKeyPath?,
    context: Context
) : ViewModel(), LogTagProvider {
    override val TAG = "KeyEditViewModel"
    private var vibrator = ContextCompat.getSystemService(context, Vibrator::class.java)
    private val lengthFilter = LengthFilter(context)

    @Inject
    lateinit var favoriteApi: FavoriteApi

    @Inject
    lateinit var parser: KeyParser

    @Inject
    lateinit var simpleKeyApi: SimpleKeyApi

    @Inject
    lateinit var updateKeyApi: UpdateKeyApi

    private val keyEditState = MutableStateFlow<KeyEditState>(KeyEditState.Loading)
    private var finishListener: FinishListener? = null

    init {
        ComponentHolder.component<KeyEditComponent>().inject(this)
        viewModelScope.launch {
            if (keyPath == null) {
                keyEditState.emit(KeyEditState.Failed)
                return@launch
            }
            val flipperKey = simpleKeyApi.getKey(keyPath)
            if (flipperKey == null) {
                keyEditState.emit(KeyEditState.Failed)
                return@launch
            }
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

    fun onSave(router: Router) {
        val savingState = keyEditState.updateAndGet {
            if (it is KeyEditState.Editing) {
                KeyEditState.Saving(it)
            } else {
                warn { "We try edit key without editing state" }
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
                if (keyPath == null) {
                    return@launch
                }
                val editState = savingState.editState
                val oldKey = simpleKeyApi.getKey(keyPath) ?: return@launch
                val extension = keyPath.path.nameWithExtension.substringAfterLast('.')
                val newFlipperKey = oldKey.copy(
                    mainFile = oldKey.mainFile.copy(
                        path = FlipperFilePath(
                            keyPath.path.folder,
                            "${editState.name}.$extension"
                        )
                    ),
                    notes = editState.notes
                )
                updateKeyApi.updateKey(oldKey, newFlipperKey)
            } finally {
                router.exit()
            }
        }
    }
}
