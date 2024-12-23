package com.flipperdevices.archive.impl.viewmodel

import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureStatus
import com.flipperdevices.bridge.connection.feature.provider.api.get
import com.flipperdevices.bridge.connection.feature.serialspeed.api.FSpeedFeatureApi
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class SpeedViewModel @Inject constructor(
    fFeatureProvider: FFeatureProvider,
) : DecomposeViewModel() {
    val speedFlow = fFeatureProvider
        .get<FSpeedFeatureApi>()
        .map { status -> status as? FFeatureStatus.Supported<FSpeedFeatureApi> }
        .flatMapLatest { status -> status?.featureApi?.getSpeed() ?: flowOf(null) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)
}
