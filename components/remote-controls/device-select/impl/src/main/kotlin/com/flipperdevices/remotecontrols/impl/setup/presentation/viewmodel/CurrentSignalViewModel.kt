package com.flipperdevices.remotecontrols.impl.setup.presentation.viewmodel

import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.ifrmvp.api.backend.ApiBackend
import com.flipperdevices.ifrmvp.backend.model.SignalRequestModel
import com.flipperdevices.ifrmvp.backend.model.SignalRequestModel.SignalResultData
import com.flipperdevices.ifrmvp.backend.model.SignalResponseModel
import com.flipperdevices.remotecontrols.api.SetupScreenDecomposeComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class CurrentSignalViewModel(
    private val apiBackend: ApiBackend,
    private val param: SetupScreenDecomposeComponent.Param
) : DecomposeViewModel() {
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
                delay(1000L)
                apiBackend.getSignal(request)
            }
        }
        result
            .onFailure { state.value = State.Error }
            .onFailure(Throwable::printStackTrace)
            .onSuccess { state.value = State.Loaded(it) }
    }

    init {
        load(emptyList(), emptyList())
    }

    sealed interface State {
        data object Loading : State
        data object Error : State
        data class Loaded(val response: SignalResponseModel) : State
    }
}
