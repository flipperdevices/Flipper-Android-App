package com.flipperdevices.connection.impl.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.api.error.FlipperBleServiceError
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.connection.impl.R
import com.flipperdevices.connection.impl.di.ConnectionComponent
import com.flipperdevices.connection.impl.model.ConnectionStatusState
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ui.AndroidLifecycleViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import no.nordicsemi.android.ble.ktx.state.ConnectionState

class ConnectionStatusViewModel(
    application: Application
) : AndroidLifecycleViewModel(application),
    FlipperBleServiceConsumer {
    private val statusState = MutableStateFlow<ConnectionStatusState>(
        ConnectionStatusState.Disconnected
    )

    @Inject
    lateinit var serviceProvider: FlipperServiceProvider

    init {
        ComponentHolder.component<ConnectionComponent>().inject(this)
        serviceProvider.provideServiceApi(consumer = this, lifecycleOwner = this)
    }

    fun getStatusState(): StateFlow<ConnectionStatusState> = statusState

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) {
        serviceApi.connectionInformationApi.getConnectionStateFlow().onEach {
            statusState.emit(
                it.toConnectionStatus(
                    serviceApi.connectionInformationApi.getConnectedDeviceName()
                )
            )
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
        Toast.makeText(application, errorTextResId, Toast.LENGTH_LONG)
    }
}

private fun ConnectionState.toConnectionStatus(deviceName: String?) = when (this) {
    ConnectionState.Connecting -> ConnectionStatusState.Connecting
    ConnectionState.Initializing -> ConnectionStatusState.Connecting
    ConnectionState.Ready -> ConnectionStatusState.Completed(deviceName ?: "Unnamed")
    ConnectionState.Disconnecting -> ConnectionStatusState.Connecting
    is ConnectionState.Disconnected -> ConnectionStatusState.Disconnected
}
