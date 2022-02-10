package com.flipperdevices.firstpair.impl.viewmodels.connecting

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
import com.flipperdevices.bridge.api.manager.ktx.stateAsFlow
import com.flipperdevices.bridge.api.scanner.DiscoveredBluetoothDevice
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.firstpair.impl.model.DevicePairState
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

private const val TIMEOUT_MS = 30L * 1000

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
    private var connectingJob: Job? = null

    fun startConnectToDevice(device: DiscoveredBluetoothDevice) {
        info { "Start connect to ${device.name} (${device.address})" }

        if (connectingJob != null) {
            return
        }
        @Suppress("TooGenericExceptionCaught", "SwallowedException")
        connectingJob = viewModelScope.launch {
            try {
                withTimeout(TIMEOUT_MS) {
                    firstPairBleManager.connectToDevice(device.device)
                }
            } catch (timeout: TimeoutCancellationException) {
                pairState.emit(DevicePairState.Timeout)
            } catch (anyOtherException: Throwable) {
                error(anyOtherException) { "Fatal exception while try connecting to device" }
                pairState.emit(DevicePairState.Timeout)
            }
        }
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
