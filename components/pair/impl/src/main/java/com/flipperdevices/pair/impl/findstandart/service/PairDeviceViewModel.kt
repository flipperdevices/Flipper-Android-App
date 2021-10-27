package com.flipperdevices.pair.impl.findstandart.service

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.provider.FlipperApi
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.pair.impl.di.PairComponent
import com.flipperdevices.pair.impl.model.findcompanion.PairingState
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import no.nordicsemi.android.ble.ktx.state.ConnectionState

class PairDeviceViewModel : ViewModel() {
    @Inject
    lateinit var bleService: FlipperServiceApi

    private val _state = MutableStateFlow<PairingState>(PairingState.NotInitialized)

    init {
        ComponentHolder.component<PairComponent>().inject(this)
    }

    fun getConnectionState(): StateFlow<PairingState> = _state

    fun startConnectToDevice(device: BluetoothDevice, onReady: () -> Unit) {
        val flipperDeviceApi = FlipperApi.flipperPairApi.getFlipperApi(context, device.address)
        viewModelScope.launch {
            flipperDeviceApi.getBleManager().getConnectionStateFlow().collect {
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
