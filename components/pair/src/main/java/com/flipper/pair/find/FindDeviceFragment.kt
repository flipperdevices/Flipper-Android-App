package com.flipper.pair.find

import android.os.Bundle
import android.view.View
import androidx.compose.runtime.Composable
import androidx.core.content.edit
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.flipper.core.di.ComponentHolder
import com.flipper.core.models.BLEDevice
import com.flipper.core.navigation.screen.InfoScreenProvider
import com.flipper.core.utils.preference.FlipperSharedPreferences
import com.flipper.core.utils.preference.FlipperSharedPreferencesKey
import com.flipper.core.view.ComposeFragment
import com.flipper.pair.di.PairComponent
import com.flipper.pair.find.compose.ComposeFindDevice
import com.flipper.pair.find.service.BLEDeviceViewModel
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class FindDeviceFragment : ComposeFragment() {
    @Inject
    lateinit var router: Router

    @Inject
    lateinit var screenProvider: InfoScreenProvider

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
        val deviceId = sharedPreferences.getString(FlipperSharedPreferencesKey.DEVICE_ID, null)
        if (deviceId != null) {
            lifecycleScope.launch {
                bleDeviceViewModel.state.collect { deviceList ->
                    val selectedDevice = deviceList.find { it.id == deviceId } ?: return@collect
                    onDeviceSelected(selectedDevice)
                }
            }
        }
    }

    private fun onDeviceSelected(bleDevice: BLEDevice) {
        bleDeviceViewModel.stopScanAndReset()
        sharedPreferences.edit { putString(FlipperSharedPreferencesKey.DEVICE_ID, bleDevice.id) }
        router.navigateTo(screenProvider.deviceInformationScreen(bleDevice))
    }
}
