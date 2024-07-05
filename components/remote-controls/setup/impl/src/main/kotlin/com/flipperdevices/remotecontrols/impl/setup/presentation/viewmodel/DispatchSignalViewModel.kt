package com.flipperdevices.remotecontrols.impl.setup.presentation.viewmodel

import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.ifrmvp.model.IfrKeyIdentifier
import com.flipperdevices.infrared.editor.model.InfraredRemote
import com.flipperdevices.keyemulate.api.EmulateHelper
import com.flipperdevices.keyemulate.model.EmulateConfig
import com.flipperdevices.remotecontrols.api.DispatchSignalApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

internal class DispatchSignalViewModel(
    private val emulateHelper: EmulateHelper,
    private val serviceProvider: FlipperServiceProvider
) : DecomposeViewModel(),
    FlipperBleServiceConsumer,
    LogTagProvider,
    DispatchSignalApi {
    override val TAG: String = "DispatchSignalViewModel"

    override val state = MutableStateFlow<DispatchSignalApi.State>(DispatchSignalApi.State.Pending)

    override fun reset() {
        state.value = DispatchSignalApi.State.Pending
    }

    override fun dispatch(config: EmulateConfig) {
        serviceProvider.provideServiceApi(
            lifecycleOwner = this,
            onError = { state.value = DispatchSignalApi.State.Error },
            onBleManager = { serviceApi ->
                viewModelScope.launch {
                    state.value = DispatchSignalApi.State.Emulating
                    kotlin.runCatching {
                        emulateHelper.startEmulate(
                            scope = this,
                            serviceApi = serviceApi,
                            config = config
                        )
                        delay(500L)
                        emulateHelper.stopEmulate(this, serviceApi.requestApi)
                    }.onFailure(Throwable::printStackTrace) // todo
                    reset()
                }
            }
        )
    }

    override fun dispatch(
        identifier: IfrKeyIdentifier,
        remotes: List<InfraredRemote>,
        fileName: String
    ) {
        val i = remotes.indexOfFirst { remote ->
            when (identifier) {
                is IfrKeyIdentifier.Name -> remote.name == identifier.name
                is IfrKeyIdentifier.Sha256 -> TODO()
            }
        }
        if (i == -1) error { "Not found!" }
        val remote = remotes[i]
        val ffPath = FlipperFilePath(
            FlipperKeyType.INFRARED.flipperDir,
            fileName
        )
        val config = EmulateConfig(
            keyPath = ffPath,
            keyType = FlipperKeyType.INFRARED,
            args = remote.name,
            index = i
        )
        dispatch(config)
    }

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) = Unit
}
