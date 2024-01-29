package com.flipperdevices.firstpair.impl.fragments.permissions

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import androidx.activity.result.ActivityResult
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.log.verbose
import com.flipperdevices.core.log.warn
import com.flipperdevices.firstpair.impl.di.FirstPairComponent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Provider

class BluetoothEnableHelper(
    private val listener: Listener
) : LogTagProvider {
    override val TAG = "BluetoothEnableHelper"

    @Inject
    lateinit var bluetoothAdapterProvider: Provider<BluetoothAdapter>

    init {
        ComponentHolder.component<FirstPairComponent>().inject(this)
    }

    private val _state = MutableStateFlow(false)
    fun state() = _state.asStateFlow()

    private val bluetoothAdapter by bluetoothAdapterProvider

    fun processBluetoothActivityResult(
        result: ActivityResult
    ) {
        _state.update { false }
        if (result.resultCode != Activity.RESULT_OK) {
            warn { "Bluetooth enable request failed, code is ${result.resultCode}" }
            listener.onBluetoothUserDenied()
            return
        }
        info { "Successful grant bluetooth permission" }
        listener.onBluetoothEnabled()
    }

    fun requestBluetoothEnable() {
        if (isBluetoothEnabled()) {
            warn { "Request bluetooth enable, but bluetooth already enabled" }
            // Already enabled bluetooth
            listener.onBluetoothEnabled()
            return
        }
        verbose { "Request bluetooth enable" }
        _state.update { true }
    }

    fun isBluetoothEnabled(): Boolean {
        return bluetoothAdapter.isEnabled
    }

    interface Listener {
        fun onBluetoothEnabled()
        fun onBluetoothUserDenied()
    }
}
