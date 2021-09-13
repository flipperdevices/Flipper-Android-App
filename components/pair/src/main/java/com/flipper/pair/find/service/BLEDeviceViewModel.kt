package com.flipper.pair.find.service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipper.bridge.api.scanner.DiscoveredBluetoothDevice
import com.flipper.bridge.impl.scanner.FlipperScannerImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

class BLEDeviceViewModel : ViewModel() {
    private val scanner = FlipperScannerImpl()
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
                Timber.e(exception, "Exception while search devices")
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
