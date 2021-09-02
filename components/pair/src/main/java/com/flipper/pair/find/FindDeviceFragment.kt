package com.flipper.pair.find

import android.os.Bundle
import android.view.View
import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
import com.flipper.core.di.ComponentHolder
import com.flipper.core.models.BLEDevice
import com.flipper.core.navigation.screen.InfoScreenProvider
import com.flipper.core.view.ComposeFragment
import com.flipper.pair.di.PairComponent
import com.flipper.pair.find.compose.ComposeFindDevice
import com.flipper.pair.find.service.BLEDeviceViewModel
import com.github.terrakok.cicerone.Router
import javax.inject.Inject

class FindDeviceFragment : ComposeFragment() {
    @Inject
    lateinit var router: Router

    @Inject
    lateinit var screenProvider: InfoScreenProvider

    private val bleDeviceViewModel by viewModels<BLEDeviceViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ComponentHolder.component<PairComponent>().inject(this)
    }

    @Composable
    override fun renderView() {
        ComposeFindDevice(bleDeviceViewModel) {
            onDeviceSelected(bleDevice = it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bleDeviceViewModel.startScanIfNotYet()
    }

    private fun onDeviceSelected(bleDevice: BLEDevice) {
        router.replaceScreen(screenProvider.deviceInformationScreen(bleDevice))
    }
}