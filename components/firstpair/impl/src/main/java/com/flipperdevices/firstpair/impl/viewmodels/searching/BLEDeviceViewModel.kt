package com.flipperdevices.firstpair.impl.viewmodels.searching

import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.api.scanner.FlipperScanner
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.firstpair.impl.model.ScanState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Provider

private const val TIMEOUT_MS = 30L * 1000

class BLEDeviceViewModel @Inject constructor(
    scannerProvider: Provider<FlipperScanner>
) : DecomposeViewModel(), LogTagProvider {
    override val TAG = "BLEDeviceViewModel"

    private val scanner by scannerProvider
    private val scanStarted = AtomicBoolean(false)
    private val state = MutableStateFlow<ScanState>(ScanState.Stopped())
    private var scanJob: Job? = null

    fun getState(): StateFlow<ScanState> = state

    @Synchronized
    fun startScanIfNotYet() {
        if (!scanStarted.compareAndSet(false, true)) {
            info { "Scan already started, skip" }
            return
        }

        scanJob = viewModelScope.launch {
            launch { startBLEDiscover() }
            delay(TIMEOUT_MS)
            // If we already 30s not found any devices
            if (state.value is ScanState.Searching) {
                state.emit(ScanState.Timeout)
                stopScan()
            }
        }
    }

    private suspend fun startBLEDiscover() = withContext(Dispatchers.Default) {
        info { "Start ble scan" }
        state.emit(ScanState.Searching)
        scanner.findFlipperDevices()
            .catch { exception ->
                error(exception) { "Exception while search devices" }
            }
            .collect { devices ->
                state.update {
                    val devicesList = devices.toList()
                    if ((it !is ScanState.Founded || it.devices != devices) &&
                        devicesList.isNotEmpty()
                    ) {
                        ScanState.Founded(devicesList)
                    } else {
                        it
                    }
                }
            }
    }

    @Synchronized
    fun stopScan() {
        if (!scanStarted.compareAndSet(true, false)) {
            return
        }
        scanJob?.cancel()
        scanJob = null
        state.update { if (it !is ScanState.Stopped) ScanState.Stopped() else it }
    }
}
