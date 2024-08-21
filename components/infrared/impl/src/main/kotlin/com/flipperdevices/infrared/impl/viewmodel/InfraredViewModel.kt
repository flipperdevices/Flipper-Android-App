package com.flipperdevices.infrared.impl.viewmodel

import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.infrared.api.InfraredConnectionApi
import com.flipperdevices.infrared.api.InfraredConnectionApi.InfraredEmulateState
import com.flipperdevices.keyscreen.api.KeyStateHelperApi
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class InfraredViewModel @AssistedInject constructor(
    @Assisted val keyPath: FlipperKeyPath, // For get value to bottom sheet
    keyStateHelperApi: KeyStateHelperApi.Builder,
    serviceProvider: FlipperServiceProvider,
    private val infraredConnectionApi: InfraredConnectionApi
) : DecomposeViewModel(), FlipperBleServiceConsumer, LogTagProvider {
    override val TAG: String = "InfraredViewModel"

    private val keyStateHelper = keyStateHelperApi.build(keyPath, viewModelScope)
    fun getState() = keyStateHelper.getKeyScreenState()

    private val emulateStateFlow = MutableStateFlow<InfraredEmulateState?>(null)
    fun getEmulateState() = emulateStateFlow.asStateFlow()

    init {
        serviceProvider.provideServiceApi(this, this)
    }

    fun setFavorite(isFavorite: Boolean) = keyStateHelper.setFavorite(isFavorite)

    fun onRename(onEndAction: (FlipperKeyPath) -> Unit) = keyStateHelper.onOpenEdit(onEndAction)

    fun onDelete(onEndAction: () -> Unit) = keyStateHelper.onDelete(onEndAction)

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) {
        infraredConnectionApi.getState(serviceApi)
            .onEach { info { "#onServiceApiReady $it" } }
            .onEach { emulateStateFlow.emit(it) }
            .launchIn(viewModelScope)
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            keyPath: FlipperKeyPath
        ): InfraredViewModel
    }
}
