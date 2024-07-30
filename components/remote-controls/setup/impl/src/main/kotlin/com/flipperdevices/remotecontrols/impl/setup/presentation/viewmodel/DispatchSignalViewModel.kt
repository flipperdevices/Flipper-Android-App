package com.flipperdevices.remotecontrols.impl.setup.presentation.viewmodel

import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.ifrmvp.model.IfrKeyIdentifier
import com.flipperdevices.infrared.editor.core.model.InfraredRemote
import com.flipperdevices.keyemulate.api.EmulateHelper
import com.flipperdevices.keyemulate.exception.AlreadyOpenedAppException
import com.flipperdevices.keyemulate.model.EmulateConfig
import com.flipperdevices.remotecontrols.api.DispatchSignalApi
import com.flipperdevices.remotecontrols.impl.setup.encoding.ByteArrayEncoder
import com.flipperdevices.remotecontrols.impl.setup.encoding.JvmEncoder
import com.flipperdevices.remotecontrols.impl.setup.util.toByteArray
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val _state = MutableStateFlow<DispatchSignalApi.State>(DispatchSignalApi.State.Pending)
    override val state = _state.asStateFlow()

    private val _isEmulated = MutableStateFlow(false)
    override val isEmulated = _isEmulated.asStateFlow()

    private var latestDispatchJob: Job? = null

    override fun reset() {
        viewModelScope.launch {
            latestDispatchJob?.cancelAndJoin()
            _state.value = DispatchSignalApi.State.Pending
            _isEmulated.value = false
        }
    }

    override fun dispatch(
        identifier: IfrKeyIdentifier,
        remotes: List<InfraredRemote>,
        ffPath: FlipperFilePath
    ) {
        val i = remotes.indexOfFirst { remote ->
            when (identifier) {
                is IfrKeyIdentifier.Name -> {
                    remote.name == identifier.name
                }

                is IfrKeyIdentifier.Sha256 -> {
                    val encoder = JvmEncoder(ByteArrayEncoder.Algorithm.SHA_256)
                    identifier.hash == encoder.encode(remote.toByteArray())
                }

                is IfrKeyIdentifier.MD5 -> {
                    val encoder = JvmEncoder(ByteArrayEncoder.Algorithm.MD5)
                    identifier.hash == encoder.encode(remote.toByteArray())
                }

                IfrKeyIdentifier.Unknown -> {
                    error { "Found Unknown key identifier on remote ${remote.name}" }
                    false
                }
            }
        }
        val remote = remotes.getOrNull(i) ?: run {
            error { "Not found remote by identifier $identifier" }
            return
        }
        info { "#dispatch remote: ${remote.name} i: $i" }
        val config = EmulateConfig(
            keyPath = ffPath,
            keyType = FlipperKeyType.INFRARED,
            args = remote.name,
            index = i
        )
        dispatch(config, identifier)
    }

    override fun dismissBusyDialog() {
        _state.value = DispatchSignalApi.State.Pending
    }

    override fun dispatch(config: EmulateConfig, identifier: IfrKeyIdentifier) {
        if (latestDispatchJob?.isActive == true) return
        latestDispatchJob = viewModelScope.launch(Dispatchers.Main) {
            _state.emit(DispatchSignalApi.State.Pending)
            serviceProvider.provideServiceApi(
                lifecycleOwner = this@DispatchSignalViewModel,
                onError = { _state.value = DispatchSignalApi.State.Error },
                onBleManager = { serviceApi ->
                    launch {
                        _state.emit(DispatchSignalApi.State.Emulating(identifier))
                        try {
                            emulateHelper.startEmulate(
                                scope = this,
                                serviceApi = serviceApi,
                                config = config
                            )
                            delay(DEFAULT_SIGNAL_DELAY)
                            emulateHelper.stopEmulate(this, serviceApi.requestApi)
                            _state.emit(DispatchSignalApi.State.Pending)
                            _isEmulated.emit(true)
                        } catch (ignored: AlreadyOpenedAppException) {
                            _state.emit(DispatchSignalApi.State.FlipperIsBusy)
                        } catch (e: Exception) {
                            error(e) { "#tryLoad uncaught exception: could not dispatch signal" }
                            _state.emit(DispatchSignalApi.State.Pending)
                        }
                    }
                }
            )
        }
    }

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) = Unit

    companion object {
        private const val DEFAULT_SIGNAL_DELAY = 500L
    }
}
