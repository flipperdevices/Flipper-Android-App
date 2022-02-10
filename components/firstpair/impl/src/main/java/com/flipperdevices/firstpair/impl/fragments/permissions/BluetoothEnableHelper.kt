package com.flipperdevices.firstpair.impl.fragments.permissions

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.log.verbose
import com.flipperdevices.core.log.warn
import com.flipperdevices.firstpair.impl.di.FirstPairComponent
import javax.inject.Inject
import javax.inject.Provider

class BluetoothEnableHelper(
    fragment: Fragment,
    private val listener: Listener
) : LogTagProvider {
    override val TAG = "BluetoothEnableHelper"

    @Inject
    lateinit var bluetoothAdapterProvider: Provider<BluetoothAdapter>

    init {
        ComponentHolder.component<FirstPairComponent>().inject(this)
    }

    private val bluetoothAdapter by bluetoothAdapterProvider

    // Result listener for bluetooth toggle
    private val bluetoothEnableWithResult = fragment.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode != Activity.RESULT_OK) {
            warn { "Bluetooth enable request failed, code is ${result.resultCode}" }
            listener.onBluetoothUserDenied()
            return@registerForActivityResult
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
        bluetoothEnableWithResult.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
    }

    fun isBluetoothEnabled(): Boolean {
        return bluetoothAdapter.isEnabled
    }

    interface Listener {
        fun onBluetoothEnabled()
        fun onBluetoothUserDenied()
    }
}
