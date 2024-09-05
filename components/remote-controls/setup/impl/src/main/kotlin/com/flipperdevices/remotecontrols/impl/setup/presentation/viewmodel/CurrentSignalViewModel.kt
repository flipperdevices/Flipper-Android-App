package com.flipperdevices.remotecontrols.impl.setup.presentation.viewmodel

import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.ifrmvp.api.infrared.InfraredBackendApi
import com.flipperdevices.ifrmvp.backend.model.SignalRequestModel
import com.flipperdevices.ifrmvp.backend.model.SignalRequestModel.SignalResultData
import com.flipperdevices.ifrmvp.backend.model.SignalResponseModel
import com.flipperdevices.remotecontrols.api.SetupScreenDecomposeComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CurrentSignalViewModel @AssistedInject constructor(
    private val infraredBackendApi: InfraredBackendApi,
    @Assisted private val param: SetupScreenDecomposeComponent.Param,
    @Assisted private val onLoaded: (SignalResponseModel) -> Unit
) : DecomposeViewModel(), LogTagProvider {
    override val TAG: String = "CurrentSignalViewModel"
    private val _state = MutableStateFlow<State>(State.Loading)
    val state = _state.asStateFlow()

    fun load(
        successResults: List<SignalResultData>,
        failedResults: List<SignalResultData>,
        skippedResults: List<SignalResultData>
    ) = viewModelScope.launch {
        _state.emit(State.Loading)
        val result = runCatching {
            val request = SignalRequestModel(
                successResults = successResults,
                failedResults = failedResults,
                skippedResults = skippedResults,
                brandId = param.brandId,
            )
            infraredBackendApi.getSignal(request)
        }
        result
            .onFailure { _state.emit(State.Error) }
            .onFailure { throwable -> error(throwable) { "#tryLoad could not load signal model" } }
            .onSuccess { _state.emit(State.Loaded(it)) }
            .onSuccess(onLoaded)
    }

    init {
        load(emptyList(), emptyList(), emptyList())
    }

    sealed interface State {
        data object Loading : State
        data object Error : State
        data class Loaded(val response: SignalResponseModel) : State
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            param: SetupScreenDecomposeComponent.Param,
            onLoaded: (SignalResponseModel) -> Unit
        ): CurrentSignalViewModel
    }
}
