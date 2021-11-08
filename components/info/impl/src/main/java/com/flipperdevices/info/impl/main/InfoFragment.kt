package com.flipperdevices.info.impl.main

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.fragment.app.viewModels
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ui.ComposeFragment
import com.flipperdevices.info.impl.di.InfoComponent
import com.flipperdevices.info.impl.main.compose.ComposeInfoScreen
import com.flipperdevices.info.impl.main.viewmodel.InfoViewModel
import com.flipperdevices.pair.api.PairComponentApi
import com.flipperdevices.pair.api.PairScreenArgument
import javax.inject.Inject

class InfoFragment : ComposeFragment() {
    @Inject
    lateinit var pairComponentApi: PairComponentApi

    private val viewModel by viewModels<InfoViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ComponentHolder.component<InfoComponent>().inject(this)
    }

    @Composable
    override fun renderView() {
        val information by viewModel.getDeviceInformation().collectAsState()
        val connectionState by viewModel.getConnectionState().collectAsState()
        ComposeInfoScreen(information, connectionState, connectionToAnotherDeviceButton = {
            pairComponentApi.openPairScreen(requireContext(), PairScreenArgument.RECONNECT_DEVICE)
            requireActivity().finish()
        }, sendManyPocket = {
            viewModel.emitManyPocketToFlipper()
        })
    }
}
