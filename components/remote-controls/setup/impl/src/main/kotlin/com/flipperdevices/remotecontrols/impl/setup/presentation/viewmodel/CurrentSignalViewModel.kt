package com.flipperdevices.remotecontrols.impl.setup.presentation.viewmodel

import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.ifrmvp.api.backend.ApiBackend
import com.flipperdevices.ifrmvp.backend.model.SignalRequestModel
import com.flipperdevices.ifrmvp.backend.model.SignalRequestModel.SignalResultData
import com.flipperdevices.ifrmvp.backend.model.SignalResponseModel
import com.flipperdevices.remotecontrols.api.SetupScreenDecomposeComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CurrentSignalViewModel @AssistedInject constructor(
    private val apiBackend: ApiBackend,
    @Assisted private val param: SetupScreenDecomposeComponent.Param,
    @Assisted private val onLoaded: (SignalResponseModel) -> Unit
) : DecomposeViewModel(), LogTagProvider {
    override val TAG: String = "CurrentSignalViewModel"
    val state = MutableStateFlow<State>(State.Loading)

    fun load(
        successResults: List<SignalResultData>,
        failedResults: List<SignalResultData>
    ) = viewModelScope.launch {
        state.value = State.Loading
        val result = kotlin.runCatching {
            val request = SignalRequestModel(
                successResults = successResults,
                failedResults = failedResults,
                categoryId = param.categoryId,
                brandId = param.brandId,
            )
            withContext(Dispatchers.IO) {
                apiBackend.getSignal(request)
            }
        }
        result
            .onFailure { state.value = State.Error }
            .onFailure { throwable -> error(throwable) { "#tryLoad could not load signal model" } }
            .onSuccess { state.value = State.Loaded(it) }
            .onSuccess(onLoaded)
    }

    init {
        load(emptyList(), emptyList())
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
