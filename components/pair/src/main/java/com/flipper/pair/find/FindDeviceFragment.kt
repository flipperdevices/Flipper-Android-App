package com.flipper.pair.find

import android.os.Bundle
import android.view.View
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import com.flipper.bridge.models.BLEDevice
import com.flipper.core.utils.toast
import com.flipper.core.view.ComposeFragment
import com.flipper.pair.find.compose.ComposeFindDevice
import com.flipper.pair.find.service.BLEDeviceViewModel

class FindDeviceFragment : ComposeFragment() {
    lateinit var bleDeviceViewModel: BLEDeviceViewModel

    @Composable
    override fun renderView() {
        ComposeFindDevice(bleDeviceViewModel) {
            onDeviceSelected(bleDevice = it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bleDeviceViewModel = ViewModelProvider(this).get(BLEDeviceViewModel::class.java)
        bleDeviceViewModel.startScanIfNotYet()
    }

    private fun onDeviceSelected(bleDevice: BLEDevice) {
        toast("Device ${bleDevice.name} selected!")
    }
}