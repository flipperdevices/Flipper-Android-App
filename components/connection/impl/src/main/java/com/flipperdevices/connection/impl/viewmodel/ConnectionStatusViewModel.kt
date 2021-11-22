package com.flipperdevices.connection.impl.viewmodel

import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.connection.impl.di.ConnectionComponent
import com.flipperdevices.connection.impl.model.ConnectionStatusState
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ui.LifecycleViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import no.nordicsemi.android.ble.ktx.state.ConnectionState

class ConnectionStatusViewModel : LifecycleViewModel(), FlipperBleServiceConsumer {
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
            statusState.emit(it.toConnectionStatus())
        }.launchIn(viewModelScope)
    }
}

private fun ConnectionState.toConnectionStatus() = when (this) {
    ConnectionState.Connecting -> ConnectionStatusState.Connecting
    ConnectionState.Initializing -> ConnectionStatusState.Connecting
    ConnectionState.Ready -> ConnectionStatusState.Completed("TEST")
    ConnectionState.Disconnecting -> ConnectionStatusState.Connecting
    is ConnectionState.Disconnected -> ConnectionStatusState.Disconnected
}
