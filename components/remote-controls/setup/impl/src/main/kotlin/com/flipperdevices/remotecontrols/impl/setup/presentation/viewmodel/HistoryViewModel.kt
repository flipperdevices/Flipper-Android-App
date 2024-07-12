package com.flipperdevices.remotecontrols.impl.setup.presentation.viewmodel

import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.ifrmvp.backend.model.SignalModel
import com.flipperdevices.ifrmvp.backend.model.SignalRequestModel.SignalResultData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class HistoryViewModel @Inject constructor() : DecomposeViewModel() {
    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    fun rememberSuccessful(signalModel: SignalModel) {
        val signalResultData = SignalResultData(
            signalId = signalModel.id,
            ifrFileId = signalModel.irFileId
        )
        _state.update { it.copy(successfulSignals = it.successfulSignals + signalResultData) }
    }

    fun rememberFailed(signalModel: SignalModel) {
        val signalResultData = SignalResultData(
            signalId = signalModel.id,
            ifrFileId = signalModel.irFileId
        )
        _state.update { it.copy(failedSignals = it.failedSignals + signalResultData) }
    }

    data class State(
        val successfulSignals: List<SignalResultData> = emptyList(),
        val failedSignals: List<SignalResultData> = emptyList()
    )
}
