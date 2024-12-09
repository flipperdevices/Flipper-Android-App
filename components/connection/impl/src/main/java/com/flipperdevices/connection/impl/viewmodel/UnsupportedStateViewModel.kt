package com.flipperdevices.connection.impl.viewmodel

import com.flipperdevices.bridge.connection.feature.protocolversion.model.FlipperSupportedState
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.connection.impl.util.getSupportedState
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class UnsupportedStateViewModel @Inject constructor(
    private val featureProvider: FFeatureProvider,
) : DecomposeViewModel() {
    private val unsupportedStateFlow = MutableStateFlow(FlipperSupportedState.READY)

    init {
        collectVersionState()
    }

    fun getUnsupportedState(): StateFlow<FlipperSupportedState> = unsupportedStateFlow

    private fun collectVersionState() {
        featureProvider.getSupportedState()
            .onEach { state -> unsupportedStateFlow.emit(state) }
            .launchIn(viewModelScope)
    }
}
