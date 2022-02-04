package com.flipperdevices.firstpair.impl.fragments

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
import com.flipperdevices.core.ui.ComposeFragment
import com.flipperdevices.firstpair.impl.composable.searching.ComposableSearchingScreen
import com.flipperdevices.firstpair.impl.viewmodels.searching.BLEDeviceViewModel

class DeviceSearchingFragment : ComposeFragment() {
    private val viewModelSearch by viewModels<BLEDeviceViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModelSearch.startScanIfNotYet()
    }

    @Composable
    override fun RenderView() {
        ComposableSearchingScreen(viewModelSearch)
    }
}
