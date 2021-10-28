package com.flipperdevices.info.main

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.fragment.app.viewModels
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.view.ComposeFragment
import com.flipperdevices.info.di.InfoComponent
import com.flipperdevices.info.main.compose.ComposeInfoScreen
import com.flipperdevices.info.main.viewmodel.InfoViewModel
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
        ComposeInfoScreen(information, connectionState) {
            pairComponentApi.openPairScreen(requireContext(), PairScreenArgument.RECONNECT_DEVICE)
            requireActivity().finish()
        }
    }
}
