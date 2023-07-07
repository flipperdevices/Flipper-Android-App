package com.flipperdevices.connection.impl.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.api.error.FlipperBleServiceError
import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
import com.flipperdevices.bridge.api.manager.ktx.state.FlipperSupportedState
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.connection.impl.BuildConfig
import com.flipperdevices.connection.impl.R
import com.flipperdevices.connection.impl.di.ConnectionComponent
import com.flipperdevices.connection.impl.model.ConnectionStatusState
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.preference.pb.PairSettings
import com.flipperdevices.core.ui.lifecycle.AndroidLifecycleViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TIMEOUT_SYNCHRONIZED_STATUS_MS = 3000L

class ConnectionStatusViewModel(
    application: Application
) : AndroidLifecycleViewModel(application),
    FlipperBleServiceConsumer {
    private val statusState = MutableStateFlow<ConnectionStatusState>(
        ConnectionStatusState.Disconnected
    )
    private var switchFromSynchronizedJob: Job? = null

    @Inject
    lateinit var serviceProvider: FlipperServiceProvider

    @Inject
    lateinit var synchronizationApi: SynchronizationApi

    @Inject
    lateinit var pairSettingsStore: DataStore<PairSettings>

    init {
        ComponentHolder.component<ConnectionComponent>().inject(this)
        serviceProvider.provideServiceApi(consumer = this, lifecycleOwner = this)
    }

    @Composable
    fun getStatusState(): StateFlow<ConnectionStatusState> = statusState

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) {
        serviceApi.connectionInformationApi.getConnectionStateFlow().combine(
            synchronizationApi.getSynchronizationState()
        ) { connectionState, synchronizationState ->
            if (connectionState is ConnectionState.Ready &&
                connectionState.supportedState == FlipperSupportedState.READY
            ) {
                return@combine synchronizationState.toConnectionStatus()
            } else {
                return@combine connectionState.toConnectionStatus()
            }
        }.onEach {
            if (it is ConnectionStatusState.Synchronized &&
                switchFromSynchronizedJob == null
            ) {
                switchFromSynchronizedJob = viewModelScope.launch(Dispatchers.Default) {
                    delay(TIMEOUT_SYNCHRONIZED_STATUS_MS)
                    statusState.update {
                        if (it is ConnectionStatusState.Synchronized) {
                            ConnectionStatusState.Connected
                        } else {
                            it
                        }
                    }
                    switchFromSynchronizedJob = null
                }
            } else {
                switchFromSynchronizedJob?.cancel()
                switchFromSynchronizedJob = null
            }
            statusState.emit(it)
        }.launchIn(viewModelScope)
    }

    override fun onServiceBleError(error: FlipperBleServiceError) {
        super.onServiceBleError(error)
        if (!BuildConfig.INTERNAL) {
            return
        }

        val errorTextResId = when (error) {
            FlipperBleServiceError.CONNECT_BLUETOOTH_DISABLED ->
                R.string.error_connect_bluetooth_disabled
            FlipperBleServiceError.CONNECT_BLUETOOTH_PERMISSION ->
                R.string.error_connect_bluetooth_permission
            FlipperBleServiceError.SERVICE_INFORMATION_NOT_FOUND ->
                R.string.error_connect_information_not_found
            FlipperBleServiceError.SERVICE_SERIAL_NOT_FOUND ->
                R.string.error_connect_serial_not_found
            FlipperBleServiceError.SERVICE_INFORMATION_FAILED_INIT ->
                R.string.error_connect_information_init_failed
            FlipperBleServiceError.SERVICE_SERIAL_FAILED_INIT ->
                R.string.error_connect_serial_init_failed
            FlipperBleServiceError.SERVICE_VERSION_NOT_FOUND ->
                R.string.error_connect_version_not_found
            FlipperBleServiceError.SERVICE_VERSION_FAILED_INIT ->
                R.string.error_connect_version_init_failed
        }
        val application = getApplication<Application>()
        viewModelScope.launch(Dispatchers.Main) {
            Toast.makeText(application, errorTextResId, Toast.LENGTH_LONG).show()
        }
    }

    private suspend fun ConnectionState.toConnectionStatus() = when (this) {
        ConnectionState.Connecting -> ConnectionStatusState.Connecting
        ConnectionState.Initializing -> ConnectionStatusState.Connecting
        ConnectionState.RetrievingInformation -> ConnectionStatusState.Connecting
        is ConnectionState.Ready -> if (supportedState == FlipperSupportedState.READY) {
            ConnectionStatusState.Connected
        } else {
            ConnectionStatusState.Unsupported
        }
        ConnectionState.Disconnecting -> ConnectionStatusState.Connecting
        is ConnectionState.Disconnected -> if (
            pairSettingsStore.data.first().deviceId.isBlank()
        ) {
            ConnectionStatusState.NoDevice
        } else {
            ConnectionStatusState.Disconnected
        }
    }
}

private fun SynchronizationState.toConnectionStatus() = when (this) {
    SynchronizationState.NotStarted -> ConnectionStatusState.Connected
    is SynchronizationState.InProgress -> ConnectionStatusState.Synchronization(progress)
    SynchronizationState.Finished -> ConnectionStatusState.Synchronized
}
