package com.flipperdevices.firstpair.impl.viewmodels.connecting

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
import com.flipperdevices.bridge.api.manager.ktx.stateAsFlow
import com.flipperdevices.bridge.api.scanner.DiscoveredBluetoothDevice
import com.flipperdevices.core.ktx.jre.launchWithLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.firstpair.impl.model.DevicePairState
import com.flipperdevices.firstpair.impl.storage.FirstPairStorage
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

private const val TIMEOUT_MS = 30L * 1000

class PairDeviceViewModel @Inject constructor(
    private val application: Application,
    private val firstPairStorage: FirstPairStorage,
    private val deviceColorSaver: DeviceColorSaver
) : DecomposeViewModel(),
    LogTagProvider {
    override val TAG = "PairDeviceViewModel"

    private var _firstPairBleManager: FirstPairBleManager? = null
    private var connectionStateUpdateJob: Job? = null
    private val firstPairBleManager
        get() = provideBleManager()
    private val pairState = MutableStateFlow<DevicePairState>(DevicePairState.NotInitialized)
    private var connectingJob: Job? = null
    private val mutex = Mutex()

    fun startConnectToDevice(device: DiscoveredBluetoothDevice) {
        info { "Start connect to ${device.name} (${device.address})" }

        if (connectingJob != null) {
            return
        }
        @Suppress("SwallowedException")
        connectingJob = viewModelScope.launch {
            try {
                pairState.emit(
                    DevicePairState.Connecting(
                        device.address,
                        device.name
                    )
                )
                withTimeout(TIMEOUT_MS) {
                    firstPairBleManager.connectToDevice(device.device)
                }

                deviceColorSaver.saveDeviceColor(device)
            } catch (timeout: TimeoutCancellationException) {
                info { "Timeout Cancellation" }
                pairState.emit(DevicePairState.TimeoutConnecting(device))
            } catch (anyOtherException: Throwable) {
                error(anyOtherException) { "Fatal exception while try connecting to device" }
                pairState.emit(DevicePairState.TimeoutPairing(device))
            } finally {
                connectingJob = null
            }
        }

        subscribeOnPairState(device)
    }

    @SuppressLint("MissingPermission")
    private fun subscribeOnPairState(
        device: DiscoveredBluetoothDevice
    ) = launchWithLock(mutex, viewModelScope) {
        connectionStateUpdateJob?.cancelAndJoin()
        connectionStateUpdateJob = firstPairBleManager.stateAsFlow().onEach {
            info { "Receive $it" }
            when (it) {
                is ConnectionState.Disconnected ->
                    pairState.update { localPairState ->
                        when (localPairState) {
                            is DevicePairState.Connecting -> DevicePairState.TimeoutPairing(device)
                            is DevicePairState.Connected,
                            DevicePairState.NotInitialized -> DevicePairState.NotInitialized

                            is DevicePairState.TimeoutConnecting,
                            is DevicePairState.TimeoutPairing -> localPairState
                        }
                    }

                ConnectionState.Connecting,
                ConnectionState.Initializing,
                ConnectionState.Disconnecting ->
                    pairState.emit(
                        DevicePairState.Connecting(
                            firstPairBleManager.bluetoothDevice?.address,
                            firstPairBleManager.bluetoothDevice?.name
                        )
                    )

                ConnectionState.RetrievingInformation ->
                    pairState.emit(
                        DevicePairState.Connecting(
                            firstPairBleManager.bluetoothDevice?.address,
                            firstPairBleManager.bluetoothDevice?.name
                        )
                    )

                is ConnectionState.Ready ->
                    pairState.emit(
                        DevicePairState.Connected(
                            firstPairBleManager.bluetoothDevice?.address,
                            firstPairBleManager.bluetoothDevice?.name
                        )
                    )
            }
        }.launchIn(viewModelScope)
    }

    fun resetConnection() = viewModelScope.launch {
        connectingJob?.cancelAndJoin()
        connectingJob = null
        pairState.emit(DevicePairState.NotInitialized)
    }

    fun getConnectionState(): StateFlow<DevicePairState> = pairState

    fun close() {
        _firstPairBleManager?.close()
        _firstPairBleManager = null
        connectionStateUpdateJob?.cancel()
        connectionStateUpdateJob = null
        pairState.update { DevicePairState.NotInitialized }
    }

    override fun onDestroy() {
        super.onDestroy()
        close()
    }

    fun finishConnection(
        deviceId: String? = null,
        deviceName: String? = null,
        onEndAction: () -> Unit,
    ) {
        close()
        firstPairStorage.markDeviceSelected(deviceId, deviceName)
        onEndAction()
    }

    @SuppressLint("MissingPermission")
    private fun provideBleManager(): FirstPairBleManager {
        var bleManager = _firstPairBleManager
        if (bleManager != null) {
            return bleManager
        }

        bleManager = FirstPairBleManager(application, viewModelScope)
        _firstPairBleManager = bleManager

        return bleManager
    }
}
