package com.flipperdevices.infrared.impl.viewmodel

import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
import com.flipperdevices.bridge.api.manager.ktx.state.FlipperSupportedState
import com.flipperdevices.bridge.api.utils.Constants.API_SUPPORTED_INFRARED_EMULATE
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.keyscreen.api.KeyStateHelperApi
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class InfraredViewModel @AssistedInject constructor(
    @Assisted val keyPath: FlipperKeyPath, // For get value to bottom sheet
    keyStateHelperApi: KeyStateHelperApi.Builder,
    private val synchronizationApi: SynchronizationApi,
    serviceProvider: FlipperServiceProvider
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
        combine(
            serviceApi.flipperVersionApi.getVersionInformationFlow(),
            serviceApi.connectionInformationApi.getConnectionStateFlow(),
            synchronizationApi.getSynchronizationState()
        ) { versionInformation, connectionState, synchronizationState ->
            return@combine if (connectionState is ConnectionState.Disconnected) {
                InfraredEmulateState.NOT_CONNECTED
            } else if (connectionState !is ConnectionState.Ready ||
                connectionState.supportedState != FlipperSupportedState.READY
            ) {
                InfraredEmulateState.CONNECTING
            } else if (synchronizationState is SynchronizationState.InProgress) {
                InfraredEmulateState.SYNCING
            } else if (versionInformation == null ||
                versionInformation < API_SUPPORTED_INFRARED_EMULATE
            ) {
                InfraredEmulateState.UPDATE_FLIPPER
            } else {
                InfraredEmulateState.ALL_GOOD
            }
        }.onEach {
            info { "#onServiceApiReady $it" }
            emulateStateFlow.emit(it)
        }.launchIn(viewModelScope)
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            keyPath: FlipperKeyPath
        ): InfraredViewModel
    }
}

enum class InfraredEmulateState {
    NOT_CONNECTED, CONNECTING, SYNCING, UPDATE_FLIPPER, ALL_GOOD
}
