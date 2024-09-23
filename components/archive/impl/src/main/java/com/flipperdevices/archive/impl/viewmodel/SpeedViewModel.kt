package com.flipperdevices.archive.impl.viewmodel

import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject

class SpeedViewModel @Inject constructor(
    private val serviceProvider: FlipperServiceProvider,
) : DecomposeViewModel() {
    val speedFlow = flow {
        serviceProvider.getServiceApi()
            .requestApi
            .getSpeed()
            .onEach { emit(it) }
            .collect()
    }.shareIn(viewModelScope, SharingStarted.Eagerly, 1)
}
