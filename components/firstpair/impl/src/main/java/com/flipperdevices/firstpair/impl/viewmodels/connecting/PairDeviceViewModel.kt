package com.flipperdevices.firstpair.impl.viewmodels.connecting

import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.BluetoothDevice
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
import com.flipperdevices.bridge.api.manager.ktx.stateAsFlow
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.firstpair.impl.model.DevicePairState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class PairDeviceViewModel(
    application: Application
) : AndroidViewModel(application),
    LogTagProvider {
    override val TAG = "PairDeviceViewModel"

    private var _firstPairBleManager: FirstPairBleManager? = null
    private var connectionStateUpdateJob: Job? = null
    private val firstPairBleManager
        get() = provideBleManager()
    private val pairState = MutableStateFlow<DevicePairState>(DevicePairState.NotInitialized)

    @SuppressLint("MissingPermission")
    fun startConnectToDevice(device: BluetoothDevice) {
        info { "Start connect to ${device.name} (${device.address})" }
        firstPairBleManager.connectToDevice(device)
    }

    fun getConnectionState(): StateFlow<DevicePairState> = pairState

    fun close() {
        _firstPairBleManager?.close()
        _firstPairBleManager = null
        connectionStateUpdateJob?.cancel()
        connectionStateUpdateJob = null
        pairState.update { DevicePairState.NotInitialized }
    }

    override fun onCleared() {
        super.onCleared()
        close()
    }

    private fun provideBleManager(): FirstPairBleManager {
        var bleManager = _firstPairBleManager
        if (bleManager != null) {
            return bleManager
        }

        bleManager = FirstPairBleManager(getApplication())
        _firstPairBleManager = bleManager

        connectionStateUpdateJob = bleManager.stateAsFlow().onEach {
            val devicePairState: DevicePairState = when (it) {
                is ConnectionState.Disconnected -> DevicePairState.NotInitialized
                ConnectionState.Connecting,
                ConnectionState.Initializing,
                ConnectionState.Disconnecting -> {
                    DevicePairState.Connecting(bleManager.bluetoothDevice?.address)
                }
                ConnectionState.Ready -> {
                    DevicePairState.Connected(bleManager.bluetoothDevice?.address)
                }
            }
            pairState.update { devicePairState }
        }.launchIn(viewModelScope)

        return bleManager
    }
}
