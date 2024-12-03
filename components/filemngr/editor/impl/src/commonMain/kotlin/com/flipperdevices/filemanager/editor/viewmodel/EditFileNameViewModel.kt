package com.flipperdevices.filemanager.editor.viewmodel

import com.flipperdevices.core.ktx.jre.FlipperFileNameValidator
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.filemanager.util.constant.FileManagerConstants
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import okio.Path

class EditFileNameViewModel @AssistedInject constructor(
    @Assisted fullPathOnFlipper: Path
) : DecomposeViewModel() {
    private val fileNameValidator = FlipperFileNameValidator()

    private val _state = MutableStateFlow(State(fullPathOnFlipper.name))
    val state = _state.asStateFlow()

    fun onChange(text: String) {
        _state.update { state ->
            state.copy(name = text)
        }
    }

    fun onOptionSelected(index: Int) {
        _state.update { state ->
            val option = state.options
                .getOrNull(index)
                ?: return@update state
            state.copy(name = "${state.name}.$option")
        }
    }

    init {
        state
            .distinctUntilChangedBy { state -> state.name }
            .onEach { state ->
                _state.emit(state.copy(isValid = fileNameValidator.isValid(state.name)))
            }.launchIn(viewModelScope)
    }

    data class State(
        val name: String = "",
        val isValid: Boolean = false,
    ) {
        val options = FileManagerConstants.FILE_EXTENSION_HINTS.toImmutableList()

        val needShowOptions
            get() = !name.contains(".") && options.isNotEmpty()
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            fullPathOnFlipper: Path,
        ): EditFileNameViewModel
    }
}
