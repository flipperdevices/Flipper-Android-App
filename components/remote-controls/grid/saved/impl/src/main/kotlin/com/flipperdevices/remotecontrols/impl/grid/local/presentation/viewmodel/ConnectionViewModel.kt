package com.flipperdevices.remotecontrols.impl.grid.local.presentation.viewmodel

import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.infrared.api.InfraredConnectionApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class ConnectionViewModel @Inject constructor(
    infraredConnectionApi: InfraredConnectionApi,
) : DecomposeViewModel() {
    val state = infraredConnectionApi.getState().stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        InfraredConnectionApi.InfraredEmulateState.ALL_GOOD
    )
}
