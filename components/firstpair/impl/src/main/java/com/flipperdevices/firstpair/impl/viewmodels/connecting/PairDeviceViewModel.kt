package com.flipperdevices.firstpair.impl.viewmodels.connecting

import android.annotation.SuppressLint
import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
import com.flipperdevices.bridge.api.manager.ktx.stateAsFlow
import com.flipperdevices.bridge.api.scanner.DiscoveredBluetoothDevice
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.ktx.jre.withLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.firstpair.impl.model.DevicePairState
import com.flipperdevices.firstpair.impl.storage.FirstPairStorage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

private const val TIMEOUT_MS = 10L * 1000

class PairDeviceViewModel(
    private val firstPairBleManagerFactory: FirstPairBleManager.Factory,
    private val firstPairStorage: FirstPairStorage,
    private val deviceColorSaver: DeviceColorSaver,
    private val dispatcher: CoroutineDispatcher
) : DecomposeViewModel(),
    LogTagProvider {

    @Inject constructor(
        firstPairBleManagerFactory: FirstPairBleManager.Factory,
        firstPairStorage: FirstPairStorage,
        deviceColorSaver: DeviceColorSaver
    ) : this(firstPairBleManagerFactory, firstPairStorage, deviceColorSaver, FlipperDispatchers.workStealingDispatcher)

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
        connectingJob = viewModelScope.launch(dispatcher) {
            try {
                pairState.emit(
                    DevicePairState.WaitingForStart(
                        device.address,
                        device.name
                    )
                )
                subscribeOnPairState(device)
                withTimeout(TIMEOUT_MS) {
                    info { "Start connect to device job" }
                    firstPairBleManager.connectToDevice(device.device)
                    info { "Finish method #connectToDevice" }
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
    }

    @SuppressLint("MissingPermission")
    private suspend fun subscribeOnPairState(
        device: DiscoveredBluetoothDevice
    ) = withLock(mutex, "subscribe_on_pairstate") {
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
                            is DevicePairState.TimeoutPairing,
                            is DevicePairState.WaitingForStart -> localPairState
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

                is ConnectionState.Ready -> {
                    pairState.emit(
                        DevicePairState.Connected(
                            firstPairBleManager.bluetoothDevice?.address,
                            firstPairBleManager.bluetoothDevice?.name
                        )
                    )
                }
            }
        }.launchIn(viewModelScope + dispatcher)
    }

    fun resetConnection() = viewModelScope.launch(dispatcher) {
        connectingJob?.cancelAndJoin()
        connectingJob = null
        pairState.emit(DevicePairState.NotInitialized)
    }

    fun getConnectionState(): StateFlow<DevicePairState> = pairState

    private fun close() {
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

        bleManager = firstPairBleManagerFactory(viewModelScope)
        _firstPairBleManager = bleManager

        return bleManager
    }
}
