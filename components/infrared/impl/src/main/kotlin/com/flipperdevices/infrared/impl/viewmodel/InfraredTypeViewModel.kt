package com.flipperdevices.infrared.impl.viewmodel

import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.api.model.UI_INFRARED_EXTENSION
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InfraredTypeViewModel @AssistedInject constructor(
    @Assisted private val flipperKeyPath: FlipperKeyPath,
    private val simpleKeyApi: SimpleKeyApi
) : DecomposeViewModel(), LogTagProvider {
    override val TAG: String = "InfraredTypeViewModel"

    private val _state = MutableStateFlow<State>(State.Loading)
    val state = _state.asStateFlow()

    fun tryLoad() {
        viewModelScope.launch {
            val apiKey = simpleKeyApi.getKey(flipperKeyPath) ?: run {
                _state.emit(State.Default)
                return@launch
            }
            val containsUiJsonFile = apiKey
                .additionalFiles
                .any { it.path.nameWithExtension.contains(UI_INFRARED_EXTENSION) }
            if (containsUiJsonFile) {
                _state.emit(State.RemoteControl)
            } else {
                _state.emit(State.Default)
            }
        }
    }

    sealed interface State {
        data object Loading : State
        data object RemoteControl : State
        data object Default : State
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            flipperKeyPath: FlipperKeyPath
        ): InfraredTypeViewModel
    }
}
