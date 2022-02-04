package com.flipperdevices.firstpair.impl.viewmodels.searching

import android.app.Application
import android.bluetooth.BluetoothDevice
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.api.manager.delegates.FlipperConnectionInformationApi
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ui.AndroidLifecycleViewModel
import com.flipperdevices.firstpair.impl.di.FirstPairComponent
import com.flipperdevices.firstpair.impl.model.PairingState
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import no.nordicsemi.android.ble.ktx.state.ConnectionState

class PairDeviceViewModel(application: Application) : AndroidLifecycleViewModel(application) {
    @Inject
    lateinit var bleService: FlipperServiceProvider
    private var deviceInternal: BluetoothDevice? = null

    private val _state = MutableStateFlow<PairingState>(PairingState.NotInitialized)

    init {
        ComponentHolder.component<FirstPairComponent>().inject(this)
    }

    fun getConnectionState(): StateFlow<PairingState> = _state

    fun startConnectToDevice(onReady: (BluetoothDevice) -> Unit) {
        val device = deviceInternal ?: error("You need call #onDeviceFounded before")
        bleService.provideServiceApi(this) { serviceApi ->
            viewModelScope.launch {
                @Suppress("TooGenericExceptionCaught")
                try {
                    serviceApi.reconnect(device)
                } catch (connectException: Exception) {
                }
                subscribeToConnectionState(serviceApi.connectionInformationApi) {
                    onReady(device)
                }
            }
        }
    }

    fun onDeviceFounded(device: BluetoothDevice) {
        deviceInternal = device
    }

    fun onStartCompanionFinding() {
        viewModelScope.launch {
            _state.emit(PairingState.FindingDevice)
        }
    }

    fun onFailedCompanionFinding(reason: String) {
        viewModelScope.launch {
            _state.emit(PairingState.Failed(reason))
        }
    }

    private fun subscribeToConnectionState(
        informationApi: FlipperConnectionInformationApi,
        onReady: () -> Unit
    ) = viewModelScope.launch {
        informationApi.getConnectionStateFlow().collect {
            _state.emit(PairingState.WithDevice(it))
            if (it == ConnectionState.Ready) {
                onReady()
            }
        }
    }
}
