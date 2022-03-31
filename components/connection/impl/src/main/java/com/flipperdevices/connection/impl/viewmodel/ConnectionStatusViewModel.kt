package com.flipperdevices.connection.impl.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bottombar.model.TabState
import com.flipperdevices.bridge.api.error.FlipperBleServiceError
import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.connection.impl.R
import com.flipperdevices.connection.impl.di.ConnectionComponent
import com.flipperdevices.connection.impl.dialog.UnsupportedDialogShowHelper
import com.flipperdevices.connection.impl.model.ConnectionStatusState
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ktx.jre.map
import com.flipperdevices.core.preference.pb.PairSettings
import com.flipperdevices.core.ui.AndroidLifecycleViewModel
import javax.inject.Inject
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

private const val TIMEOUT_SYNCHRONIZED_STATUS_MS = 3000L

class ConnectionStatusViewModel(
    application: Application
) : AndroidLifecycleViewModel(application),
    FlipperBleServiceConsumer {
    private val statusState = MutableStateFlow<ConnectionStatusState>(
        ConnectionStatusState.Disconnected
    )
    private val unsupportedDialogShowHelper = UnsupportedDialogShowHelper()
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

    fun getStatusState(): StateFlow<TabState> = statusState.map(viewModelScope) {
        ConnectionTabStateMapper.getConnectionTabState(it)
    }

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) {
        serviceApi.connectionInformationApi.getConnectionStateFlow().combine(
            synchronizationApi.getSynchronizationState()
        ) { connectionState, synchronizationState ->
            if (connectionState is ConnectionState.Ready && connectionState.isSupported) {
                return@combine synchronizationState.toConnectionStatus()
            } else {
                return@combine connectionState.toConnectionStatus()
            }
        }.onEach {
            if (it is ConnectionStatusState.Unsupported) {
                unsupportedDialogShowHelper.showDialog()
            }
            if (it is ConnectionStatusState.Synchronized &&
                switchFromSynchronizedJob == null
            ) {
                switchFromSynchronizedJob = viewModelScope.launch {
                    delay(TIMEOUT_SYNCHRONIZED_STATUS_MS)
                    statusState.update {
                        if (it is ConnectionStatusState.Synchronized) {
                            ConnectionStatusState.Connected
                        } else it
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
        val errorTextResId = when (error) {
            FlipperBleServiceError.CONNECT_BLUETOOTH_DISABLED ->
                R.string.error_connect_bluetooth_disabled
            FlipperBleServiceError.CONNECT_BLUETOOTH_PERMISSION ->
                R.string.error_connect_bluetooth_permission
            FlipperBleServiceError.CONNECT_TIMEOUT ->
                R.string.error_connect_timeout
            FlipperBleServiceError.CONNECT_REQUIRE_REBOUND ->
                R.string.error_connect_require_rebound
            FlipperBleServiceError.SERVICE_INFORMATION_NOT_FOUND ->
                R.string.error_connect_information_not_found
            FlipperBleServiceError.SERVICE_SERIAL_NOT_FOUND ->
                R.string.error_connect_serial_not_found
            FlipperBleServiceError.SERVICE_INFORMATION_FAILED_INIT ->
                R.string.error_connect_information_init_failed
            FlipperBleServiceError.SERVICE_SERIAL_FAILED_INIT ->
                R.string.error_connect_serial_init_failed
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
        is ConnectionState.Ready -> if (isSupported) ConnectionStatusState.Connected
        else ConnectionStatusState.Unsupported
        ConnectionState.Disconnecting -> ConnectionStatusState.Connecting
        is ConnectionState.Disconnected -> if (
            pairSettingsStore.data.first().deviceId.isBlank()
        ) ConnectionStatusState.NoDevice
        else ConnectionStatusState.Disconnected
    }
}

private fun SynchronizationState.toConnectionStatus() = when (this) {
    SynchronizationState.NOT_STARTED -> ConnectionStatusState.Connected
    SynchronizationState.IN_PROGRESS -> ConnectionStatusState.Synchronization
    SynchronizationState.FINISHED -> ConnectionStatusState.Synchronized
}
