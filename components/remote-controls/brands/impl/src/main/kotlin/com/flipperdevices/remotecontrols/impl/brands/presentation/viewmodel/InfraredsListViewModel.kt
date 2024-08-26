package com.flipperdevices.remotecontrols.impl.brands.presentation.viewmodel

import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.ifrmvp.api.infrared.InfraredBackendApi
import com.flipperdevices.ifrmvp.backend.model.IfrFileModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class InfraredsListViewModel @AssistedInject constructor(
    private val infraredBackendApi: InfraredBackendApi,
    @Assisted private val brandId: Long
) : DecomposeViewModel(), LogTagProvider {
    override val TAG: String = "InfraredsListViewModel"

    private val _state = MutableStateFlow<State>(State.Loading)
    val state = _state.asStateFlow()

    fun tryLoad() = viewModelScope.launch {
        _state.update { State.Loading }
        runCatching { infraredBackendApi.getInfrareds(brandId) }
            .onSuccess { _state.emit(State.Loaded(it.infraredFiles.toImmutableList())) }
            .onFailure { _state.emit(State.Error) }
            .onFailure { throwable -> error(throwable) { "#tryLoad could not load brands" } }
    }

    init {
        tryLoad()
    }

    sealed interface State {
        data object Loading : State
        data class Loaded(val infrareds: ImmutableList<IfrFileModel>) : State
        data object Error : State
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            brandId: Long
        ): InfraredsListViewModel
    }
}
