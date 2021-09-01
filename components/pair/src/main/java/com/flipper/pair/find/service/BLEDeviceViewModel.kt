package com.flipper.pair.find.service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipper.bridge.impl.scanner.FlipperScannerImpl
import com.flipper.core.models.BLEDevice
import kotlinx.coroutines.Dispatchers
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
    private val _state = MutableStateFlow(emptyList<BLEDevice>())

    val state: StateFlow<List<BLEDevice>>
        get() = _state

    fun startScanIfNotYet() = viewModelScope.launch {
        if (!scanStarted.compareAndSet(false, true)) {
            return@launch
        }
        startBLEDiscover()
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

    private suspend fun emitState(devices: Iterable<BLEDevice>) =
        withContext(viewModelScope.coroutineContext) {
            if (state.value != devices) { // Change state only if list change
                _state.emit(devices.toList())
            }
        }
}