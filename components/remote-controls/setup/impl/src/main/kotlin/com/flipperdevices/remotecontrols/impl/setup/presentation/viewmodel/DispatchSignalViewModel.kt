package com.flipperdevices.remotecontrols.impl.setup.presentation.viewmodel

import android.content.Context
import android.os.Vibrator
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.android.vibrateCompat
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.faphub.target.api.FlipperTargetProviderApi
import com.flipperdevices.faphub.target.model.FlipperTarget
import com.flipperdevices.ifrmvp.model.IfrKeyIdentifier
import com.flipperdevices.infrared.editor.core.model.InfraredRemote
import com.flipperdevices.keyemulate.api.EmulateHelper
import com.flipperdevices.keyemulate.exception.AlreadyOpenedAppException
import com.flipperdevices.keyemulate.model.EmulateConfig
import com.flipperdevices.keyemulate.tasks.CloseEmulateAppTaskHolder
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@ContributesBinding(AppGraph::class, DispatchSignalApi::class)
class DispatchSignalViewModel @Inject constructor(
    private val emulateHelper: EmulateHelper,
    private val serviceProvider: FlipperServiceProvider,
    private val closeEmulateAppTaskHolder: CloseEmulateAppTaskHolder,
    private val flipperTargetProviderApi: FlipperTargetProviderApi,
    private val settings: DataStore<Settings>,
    private val context: Context
) : DecomposeViewModel(),
    FlipperBleServiceConsumer,
    LogTagProvider,
    DispatchSignalApi {
    override val TAG: String = "DispatchSignalViewModel"

    private val vibrator = ContextCompat.getSystemService(context, Vibrator::class.java)

    private val _state = MutableStateFlow<DispatchSignalApi.State>(DispatchSignalApi.State.Pending)
    override val state = _state.asStateFlow()

    private var latestDispatchJob: Job? = null

    override fun reset() {
        viewModelScope.launch {
            latestDispatchJob?.cancelAndJoin()
            _state.value = DispatchSignalApi.State.Pending
        }
    }

    override fun dispatch(
        identifier: IfrKeyIdentifier,
        isOneTime: Boolean,
        remotes: List<InfraredRemote>,
        ffPath: FlipperFilePath,
        onDispatched: () -> Unit
    ) {
        when (flipperTargetProviderApi.getFlipperTarget().value) {
            FlipperTarget.NotConnected -> {
                _state.update { DispatchSignalApi.State.FlipperNotConnected }
                return
            }

            FlipperTarget.Unsupported -> {
                _state.update { DispatchSignalApi.State.FlipperNotSupported }
                return
            }

            else -> Unit
        }
        val i = remotes.indexOfFirst { remote ->
            when (identifier) {
                is IfrKeyIdentifier.Name -> {
                    remote.name == identifier.name
                }

                is IfrKeyIdentifier.Sha256 -> {
                    val encoder = JvmEncoder(ByteArrayEncoder.Algorithm.SHA_256)
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
        dispatch(
            config = config,
            identifier = identifier,
            isOneTime = isOneTime,
            onDispatched = onDispatched
        )
    }

    override fun dismissBusyDialog() {
        _state.value = DispatchSignalApi.State.Pending
    }

    override fun dispatch(
        config: EmulateConfig,
        identifier: IfrKeyIdentifier,
        isOneTime: Boolean,
        onDispatched: () -> Unit
    ) {
        if (latestDispatchJob?.isActive == true) return
        latestDispatchJob = viewModelScope.launch(Dispatchers.Main) {
            _state.emit(DispatchSignalApi.State.Pending)
            serviceProvider.provideServiceApi(
                lifecycleOwner = this@DispatchSignalViewModel,
                onError = { _state.value = DispatchSignalApi.State.Error },
                onBleManager = { serviceApi ->
                    launch {
                        vibrator?.vibrateCompat(
                            VIBRATOR_TIME,
                            settings.data.first().disabled_vibration
                        )
                        _state.emit(DispatchSignalApi.State.Emulating(identifier))
                        try {
                            emulateHelper.startEmulate(
                                scope = this,
                                serviceApi = serviceApi,
                                config = config
                            )
                            if (isOneTime) {
                                delay(DEFAULT_SIGNAL_DELAY)
                                emulateHelper.stopEmulate(this, serviceApi.requestApi)
                                _state.emit(DispatchSignalApi.State.Pending)
                                onDispatched.invoke()
                            }
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

    override fun stopEmulate() {
        viewModelScope.launch(Dispatchers.Main) {
            serviceProvider.provideServiceApi(
                lifecycleOwner = this@DispatchSignalViewModel,
                onError = { _state.value = DispatchSignalApi.State.Error },
                onBleManager = { serviceApi ->
                    launch {
                        vibrator?.vibrateCompat(
                            VIBRATOR_TIME,
                            settings.data.first().disabled_vibration
                        )
                        emulateHelper.stopEmulate(this, serviceApi.requestApi)
                        _state.emit(DispatchSignalApi.State.Pending)
                    }
                }
            )
        }
    }

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) = Unit

    override fun onDestroy() {
        if (_state.value is DispatchSignalApi.State.Emulating) {
            closeEmulateAppTaskHolder.closeEmulateApp(serviceProvider, emulateHelper)
        }
        super<DecomposeViewModel>.onDestroy()
    }

    companion object {
        private const val DEFAULT_SIGNAL_DELAY = 500L
        private const val VIBRATOR_TIME = 100L
    }
}
