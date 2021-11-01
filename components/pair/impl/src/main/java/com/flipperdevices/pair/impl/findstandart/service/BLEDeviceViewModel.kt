package com.flipperdevices.pair.impl.findstandart.service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.api.scanner.DiscoveredBluetoothDevice
import com.flipperdevices.bridge.provider.FlipperApi
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BLEDeviceViewModel : ViewModel(), LogTagProvider {
    override val TAG = "BLEDeviceViewModel"
    private val scanner = FlipperApi.flipperScanner
    private val scanStarted = AtomicBoolean(false)
    private val _state = MutableStateFlow(emptyList<DiscoveredBluetoothDevice>())
    private var scanJob: Job? = null

    val state: StateFlow<List<DiscoveredBluetoothDevice>>
        get() = _state

    fun startScanIfNotYet() {
        if (!scanStarted.compareAndSet(false, true)) {
            return
        }

        scanJob = viewModelScope.launch {
            startBLEDiscover()
        }
    }

    private suspend fun startBLEDiscover() = withContext(Dispatchers.IO) {
        scanner.findFlipperDevices()
            .catch { exception ->
                error(exception) { "Exception while search devices" }
            }
            .collect {
                emitState(it)
            }
    }

    private suspend fun emitState(devices: Iterable<DiscoveredBluetoothDevice>) =
        withContext(viewModelScope.coroutineContext) {
            if (state.value != devices) { // Change state only if list change
                _state.emit(devices.toList())
            }
        }

    fun stopScanAndReset() {
        if (!scanStarted.compareAndSet(true, false)) {
            return
        }
        scanJob?.cancel()
        scanJob = null
        viewModelScope.launch {
            emitState(emptyList())
        }
    }
}
