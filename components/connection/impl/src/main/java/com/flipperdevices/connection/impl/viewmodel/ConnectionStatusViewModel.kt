package com.flipperdevices.connection.impl.viewmodel

import android.app.Application
import android.content.SharedPreferences
import android.widget.Toast
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
import com.flipperdevices.connection.impl.model.ConnectionStatusState
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.preference.FlipperSharedPreferencesKey
import com.flipperdevices.core.ui.AndroidLifecycleViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ConnectionStatusViewModel(
    application: Application
) : AndroidLifecycleViewModel(application),
    FlipperBleServiceConsumer {
    private val statusState = MutableStateFlow(
        ConnectionTabStateMapper.getConnectionTabState(ConnectionStatusState.Disconnected)
    )

    @Inject
    lateinit var serviceProvider: FlipperServiceProvider

    @Inject
    lateinit var synchronizationApi: SynchronizationApi

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    init {
        ComponentHolder.component<ConnectionComponent>().inject(this)
        serviceProvider.provideServiceApi(consumer = this, lifecycleOwner = this)
    }

    fun getStatusState(): StateFlow<TabState> = statusState

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) {
        serviceApi.connectionInformationApi.getConnectionStateFlow().combine(
            synchronizationApi.getSynchronizationState()
        ) { connectionState, synchronizationState ->
            val deviceName = serviceApi.connectionInformationApi.getConnectedDeviceName()
            if (connectionState == ConnectionState.Ready) {
                return@combine synchronizationState.toConnectionStatus(deviceName)
            } else {
                return@combine connectionState.toConnectionStatus(deviceName)
            }
        }.onEach {
            statusState.emit(ConnectionTabStateMapper.getConnectionTabState(it))
        }.launchIn(viewModelScope)
    }

    override fun onServiceBleError(error: FlipperBleServiceError) {
        super.onServiceBleError(error)
        val errorTextResId = when (error) {
            FlipperBleServiceError.CONNECT_BLUETOOTH_DISABLED ->
                R.string.error_connect_bluetooth_disabled
            FlipperBleServiceError.CONNECT_DEVICE_NOT_STORED ->
                R.string.error_connect_device_not_stored
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
        Toast.makeText(application, errorTextResId, Toast.LENGTH_LONG).show()
    }

    private fun ConnectionState.toConnectionStatus(deviceName: String?) = when (this) {
        ConnectionState.Connecting -> ConnectionStatusState.Connecting
        ConnectionState.Initializing -> ConnectionStatusState.Connecting
        ConnectionState.Ready -> ConnectionStatusState.Completed(deviceName ?: "Unnamed")
        ConnectionState.Disconnecting -> ConnectionStatusState.Connecting
        is ConnectionState.Disconnected -> if (
            sharedPreferences.getString(FlipperSharedPreferencesKey.DEVICE_ID, null) == null
        ) ConnectionStatusState.NoDevice
        else ConnectionStatusState.Disconnected
    }
}

private fun SynchronizationState.toConnectionStatus(deviceName: String?) = when (this) {
    SynchronizationState.NOT_STARTED -> ConnectionStatusState.Connected
    SynchronizationState.IN_PROGRESS -> ConnectionStatusState.Synchronization
    SynchronizationState.FINISHED -> ConnectionStatusState.Completed(deviceName ?: "Unnamed")
}
