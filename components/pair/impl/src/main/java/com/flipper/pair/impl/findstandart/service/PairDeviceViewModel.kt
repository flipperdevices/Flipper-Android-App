package com.flipper.pair.impl.findstandart.service

import android.app.Application
import android.bluetooth.BluetoothDevice
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.flipper.bridge.api.FlipperApi
import com.flipper.pair.impl.model.findcompanion.PairingState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import no.nordicsemi.android.ble.ktx.state.ConnectionState
import no.nordicsemi.android.ble.ktx.stateAsFlow

class PairDeviceViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application
    private val _state = MutableStateFlow<PairingState>(PairingState.NotInitialized)

    fun getConnectionState(): StateFlow<PairingState> = _state

    fun startConnectToDevice(device: BluetoothDevice, onReady: () -> Unit) {
        val flipperDeviceApi = FlipperApi.flipperPairApi.getFlipperApi(context, device.address)
        viewModelScope.launch {
            flipperDeviceApi.getBleManager().stateAsFlow().collect {
                _state.emit(PairingState.WithDevice(it))
                if (it == ConnectionState.Ready) {
                    onReady()
                }
            }
        }
        FlipperApi.flipperPairApi.scheduleConnect(flipperDeviceApi, device)
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
