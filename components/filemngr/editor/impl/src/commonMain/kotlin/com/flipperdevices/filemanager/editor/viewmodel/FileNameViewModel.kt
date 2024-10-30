package com.flipperdevices.filemanager.editor.viewmodel

import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.core.ktx.jre.FlipperFileNameValidator
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class FileNameViewModel @Inject constructor() : DecomposeViewModel() {

    private val fileNameValidator = FlipperFileNameValidator()

    private val _state = MutableStateFlow<State>(State.Pending)
    val state = _state.asStateFlow()

    fun onChange(text: String) {
        _state.update { state ->
            (state as? State.Editing)
                ?.copy(name = text)
                ?: state
        }
    }

    fun dismiss() {
        _state.update { State.Pending }
    }

    fun onOptionSelected(index: Int) {
        _state.update { state ->
            (state as? State.Editing)?.let { editingState ->
                val option = editingState.options
                    .getOrNull(index)
                    ?: return@let editingState
                editingState.copy(name = option)
            } ?: state
        }
    }

    fun show() {
        _state.update { State.Editing() }
    }

    init {
        state
            .filterIsInstance<State.Editing>()
            .distinctUntilChangedBy { state -> state.name }
            .onEach { state ->
                _state.emit(state.copy(isValid = fileNameValidator.isValid(state.name)))
            }.launchIn(viewModelScope)
    }

    sealed interface State {
        data object Pending : State
        data class Editing(
            val name: String = "",
            val isValid: Boolean = false
        ) : State {
            val options = listOf("txt")
                .plus(FlipperKeyType.entries.map { it.extension })
                .map { extension -> "$name.$extension" }
                .toImmutableList()

            val needShowOptions
                get() = !name.contains(".") && options.isNotEmpty()
        }
    }
}
