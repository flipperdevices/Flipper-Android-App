package com.flipperdevices.remotecontrols.impl.grid.remote.presentation.viewmodel

import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.infrared.api.InfraredConnectionApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class ConnectionViewModel @Inject constructor(
    private val infraredConnectionApi: InfraredConnectionApi,
    serviceProvider: FlipperServiceProvider,
) : DecomposeViewModel() {
    val state = flow {
        val serviceApi = serviceProvider.getServiceApi()
        infraredConnectionApi.getState(serviceApi).collect { emit(it) }
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        InfraredConnectionApi.InfraredEmulateState.ALL_GOOD
    )
}
