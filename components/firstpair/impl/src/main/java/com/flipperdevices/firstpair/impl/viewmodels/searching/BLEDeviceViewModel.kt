package com.flipperdevices.firstpair.impl.viewmodels.searching

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.api.scanner.DiscoveredBluetoothDevice
import com.flipperdevices.bridge.api.scanner.FlipperScanner
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.firstpair.impl.di.FirstPairComponent
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Provider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BLEDeviceViewModel : ViewModel(), LogTagProvider {
    override val TAG = "BLEDeviceViewModel"

    @Inject
    lateinit var scannerProvider: Provider<FlipperScanner>

    init {
        ComponentHolder.component<FirstPairComponent>().inject(this)
    }

    private val scanner by scannerProvider
    private val scanStarted = AtomicBoolean(false)
    private val state = MutableStateFlow(emptyList<DiscoveredBluetoothDevice>())

    fun getState(): StateFlow<List<DiscoveredBluetoothDevice>> = state

    fun startScanIfNotYet() {
        if (!scanStarted.compareAndSet(false, true)) {
            return
        }

        viewModelScope.launch {
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
                state.emit(devices.toList())
            }
        }
}
