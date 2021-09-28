package com.flipper.pair.findstandart.service

import android.app.Application
import android.bluetooth.BluetoothDevice
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.flipper.bridge.api.FlipperApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import no.nordicsemi.android.ble.ktx.state.ConnectionState
import no.nordicsemi.android.ble.ktx.stateAsFlow

class PairDeviceViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application
    private val _state = MutableStateFlow<ConnectionState?>(null)
    private val _errorTextState = MutableStateFlow<String?>(null)

    fun getConnectionState(): StateFlow<ConnectionState?> = _state
    fun getErrorState(): StateFlow<String?> = _errorTextState

    fun startConnectToDevice(device: BluetoothDevice, onReady: () -> Unit) {
        val flipperDeviceApi = FlipperApi.flipperPairApi.getFlipperApi(context, device.address)
        viewModelScope.launch {
            flipperDeviceApi.getBleManager().stateAsFlow().collect {
                _state.emit(it)
                if (it == ConnectionState.Ready) {
                    onReady()
                }
            }
        }
        FlipperApi.flipperPairApi.scheduleConnect(flipperDeviceApi, device)
    }

    fun onStartCompanionFinding() {
        viewModelScope.launch {
            _errorTextState.emit(null)
            _state.emit(ConnectionState.Connecting)
        }
    }

    fun onFailedCompanionFinding(reason: String) {
        viewModelScope.launch {
            _errorTextState.emit(reason)
            _state.emit(null)
        }
    }
}
