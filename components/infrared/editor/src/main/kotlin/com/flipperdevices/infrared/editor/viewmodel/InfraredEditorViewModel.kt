package com.flipperdevices.infrared.editor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.dao.api.delegates.KeyParser
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.UpdateKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.infrared.editor.api.EXTRA_KEY_PATH
import com.flipperdevices.infrared.editor.model.InfraredEditorState
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tangle.inject.TangleParam
import tangle.viewmodel.VMInject

class InfraredEditorViewModel @VMInject constructor(
    @TangleParam(EXTRA_KEY_PATH)
    private val flipperKeyPath: FlipperKeyPath,
    private val keyParser: KeyParser,
    private val updateKeyApi: UpdateKeyApi,
    private val synchronizationApi: SynchronizationApi,
    private val simpleKeyApi: SimpleKeyApi,
) : ViewModel(), LogTagProvider {
    override val TAG = "InfraredEditorViewModel"

    private var startFlipperKeyParsed: FlipperKeyParsed.Infrared? = null
    private var startFlipperKey: FlipperKey? = null
    private val infraredControlFlow = MutableStateFlow<InfraredEditorState>(InfraredEditorState.Loading)
    private val showOnSaveDialogState = MutableStateFlow(false)

    fun getInfraredControlState() = infraredControlFlow.asStateFlow()
    fun getShowOnSaveDialogState(): StateFlow<Boolean> = showOnSaveDialogState
    fun dismissDialog() = showOnSaveDialogState.update { false }

    init { processLoadingKey() }

    private fun processLoadingKey() {
        viewModelScope.launch(Dispatchers.Default) {
            val flipperKey = simpleKeyApi.getKey(flipperKeyPath)
            if (flipperKey == null) {
                infraredControlFlow.emit(InfraredEditorState.Error)
                return@launch
            }

            val parsedKey = keyParser.parseKey(flipperKey)
            if (parsedKey !is FlipperKeyParsed.Infrared) {
                infraredControlFlow.emit(InfraredEditorState.Error)
                return@launch
            }

            startFlipperKeyParsed = parsedKey
            startFlipperKey = flipperKey

            val controls = parsedKey.remotes.toImmutableList()
            val name = flipperKey.path.nameWithoutExtension
            infraredControlFlow.emit(InfraredEditorState.LoadedKey(name, controls))
        }
    }

    fun onSave(onEndAction: () -> Unit) {
        viewModelScope.launch {
            if (isDirtyKey().not()) {
                onEndAction()
                return@launch
            }

            val state = getInfraredControlState().value
            if (state !is InfraredEditorState.LoadedKey) return@launch

            val flipperKey = startFlipperKey ?: return@launch
            val newFlipperKey = InfraredEditorSaver.newFlipperKey(flipperKey, state.remotes)

            updateKeyApi.updateKey(flipperKey, newFlipperKey)
            synchronizationApi.startSynchronization(force = true)
            onEndAction()
        }
    }

    fun onCancel(onCancel: () -> Unit) {
        viewModelScope.launch {
            if (isDirtyKey()) {
                showOnSaveDialogState.emit(true)
            } else {
                onCancel()
            }
        }
    }

    fun onChangePosition(from: Int, to: Int) {
        viewModelScope.launch {
            val state = getInfraredControlState().value
            if (state !is InfraredEditorState.LoadedKey) return@launch

            val fromItem = state.remotes[from]
            val toItem = state.remotes[to]

            val newRemotes = state.remotes
                .toMutableList()
                .apply {
                    set(from, toItem)
                    set(to, fromItem)
                }
                .toImmutableList()

            infraredControlFlow.emit(InfraredEditorState.LoadedKey(state.name, newRemotes))
        }
    }

    private fun isDirtyKey(): Boolean {
        val state = getInfraredControlState().value
        if (state !is InfraredEditorState.LoadedKey) return false

        val currentRemotes = state.remotes
        val initRemotes = startFlipperKeyParsed?.remotes ?: return false

        return currentRemotes != initRemotes
    }
}
