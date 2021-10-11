package com.flipperdevices.pair.impl.findstandart

import android.os.Bundle
import android.view.View
import androidx.compose.runtime.Composable
import androidx.core.content.edit
import androidx.fragment.app.viewModels
import com.flipperdevices.bridge.api.scanner.DiscoveredBluetoothDevice
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.utils.preference.FlipperSharedPreferences
import com.flipperdevices.core.utils.preference.FlipperSharedPreferencesKey
import com.flipperdevices.core.view.ComposeFragment
import com.flipperdevices.pair.impl.di.PairComponent
import com.flipperdevices.pair.impl.findstandart.compose.ComposeFindDevice
import com.flipperdevices.pair.impl.findstandart.service.BLEDeviceViewModel
import com.flipperdevices.pair.impl.navigation.machine.PairScreenStateDispatcher
import javax.inject.Inject

class StandartFindFragment : ComposeFragment() {
    @Inject
    lateinit var stateDispatcher: PairScreenStateDispatcher

    @Inject
    lateinit var sharedPreferences: FlipperSharedPreferences

    private val bleDeviceViewModel by viewModels<BLEDeviceViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ComponentHolder.component<PairComponent>().inject(this)
    }

    @Composable
    override fun renderView() {
        ComposeFindDevice(bleDeviceViewModel) { bleDevice ->
            onDeviceSelected(bleDevice)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bleDeviceViewModel.startScanIfNotYet()
    }

    private fun onDeviceSelected(discoveredBluetoothDevice: DiscoveredBluetoothDevice) {
        bleDeviceViewModel.stopScanAndReset()
        sharedPreferences.edit {
            putString(
                FlipperSharedPreferencesKey.DEVICE_ID,
                discoveredBluetoothDevice.address
            )
        }
        stateDispatcher.invalidateCurrentState { it.copy(devicePaired = true) }
    }
}
