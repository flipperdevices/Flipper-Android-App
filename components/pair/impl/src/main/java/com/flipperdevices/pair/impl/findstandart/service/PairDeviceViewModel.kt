package com.flipperdevices.pair.impl.findstandart.service

import android.app.Application
import android.bluetooth.BluetoothDevice
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.provider.FlipperApi
import com.flipperdevices.pair.impl.model.findcompanion.PairingState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import no.nordicsemi.android.ble.ktx.state.ConnectionState

class PairDeviceViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application
    private var deviceInternal: BluetoothDevice? = null
    private val _state = MutableStateFlow<PairingState>(PairingState.NotInitialized)

    fun getConnectionState(): StateFlow<PairingState> = _state

    fun startConnectToDevice(onReady: (BluetoothDevice) -> Unit) {
        val device = deviceInternal ?: error("You need call #onDeviceFounded before")
        val flipperDeviceApi = FlipperApi.flipperPairApi.getFlipperApi(context, device.address)
        viewModelScope.launch {
            flipperDeviceApi.getBleManager().getConnectionStateFlow().collect {
                _state.emit(PairingState.WithDevice(it))
                if (it == ConnectionState.Ready) {
                    onReady(device)
                }
            }
        }
        FlipperApi.flipperPairApi.scheduleConnect(flipperDeviceApi, device)
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
}
