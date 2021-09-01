package com.flipper.info.main

import android.os.Bundle
import android.view.View
import androidx.compose.runtime.Composable
import androidx.fragment.app.activityViewModels
import com.flipper.bridge.impl.viewmodel.FlipperViewModel
import com.flipper.core.models.BLEDevice
import com.flipper.core.view.ComposeFragment
import com.flipper.info.main.compose.ComposeInfoScreen

class InfoFragment : ComposeFragment() {
    private val bleViewModel by activityViewModels<FlipperViewModel>()

    @Composable
    override fun renderView() {
        ComposeInfoScreen()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bleViewModel.connect(getDevice().getBluetoothDevice())
    }

    companion object {
        const val EXTRA_DEVICE_KEY = "device"
    }

    private fun getDevice(): BLEDevice {
        return arguments?.get(EXTRA_DEVICE_KEY) as BLEDevice
    }
}