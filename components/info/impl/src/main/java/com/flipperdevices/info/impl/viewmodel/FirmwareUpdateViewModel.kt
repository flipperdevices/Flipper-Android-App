package com.flipperdevices.info.impl.viewmodel

import com.flipperdevices.bridge.connection.feature.protocolversion.api.FVersionFeatureApi
import com.flipperdevices.bridge.connection.feature.protocolversion.model.FlipperSupportedState
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureStatus
import com.flipperdevices.bridge.connection.feature.provider.api.get
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class FirmwareUpdateViewModel @Inject constructor(
    private val fFeatureProvider: FFeatureProvider
) : DecomposeViewModel(), LogTagProvider {
    override val TAG = "FirmwareUpdateViewModel"

    fun getState(): StateFlow<FlipperSupportedState> {
        return fFeatureProvider.get<FVersionFeatureApi>().flatMapLatest { status ->
            when (status) {
                FFeatureStatus.NotFound -> flowOf(FlipperSupportedState.DEPRECATED_FLIPPER)
                FFeatureStatus.Unsupported -> flowOf(FlipperSupportedState.DEPRECATED_FLIPPER)
                FFeatureStatus.Retrieving -> flowOf(FlipperSupportedState.READY)
                is FFeatureStatus.Supported -> {
                    status.featureApi
                        .getSupportedStateFlow()
                        .map { state -> state ?: FlipperSupportedState.DEPRECATED_FLIPPER }
                }
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, FlipperSupportedState.READY)
    }
}
