package com.flipperdevices.remotecontrols.impl.setup.presentation.viewmodel

import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.ifrmvp.model.IfrKeyIdentifier
import com.flipperdevices.infrared.editor.model.InfraredRemote
import com.flipperdevices.keyemulate.api.EmulateHelper
import com.flipperdevices.keyemulate.model.EmulateConfig
import com.flipperdevices.remotecontrols.api.DispatchSignalApi
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@ContributesBinding(AppGraph::class, DispatchSignalApi::class)
class DispatchSignalViewModel @Inject constructor(
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
        viewModelScope.launch(Dispatchers.Main) {
            serviceProvider.provideServiceApi(
                lifecycleOwner = this@DispatchSignalViewModel,
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
                            delay(DEFAULT_SIGNAL_DELAY)
                            emulateHelper.stopEmulate(this, serviceApi.requestApi)
                        }.onFailure { throwable -> error(throwable) { "#tryLoad could not dispatch signal" } }
                        reset()
                    }
                }
            )
        }
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
        val remote = remotes.getOrNull(i) ?: run {
            error { "Not found!" }
            return
        }
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

    companion object {
        private const val DEFAULT_SIGNAL_DELAY = 500L
    }
}
