package com.flipperdevices.info.impl.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.fragment.app.viewModels
import com.flipperdevices.core.ui.ComposeFragment
import com.flipperdevices.info.impl.main.compose.ComposeInfoScreen
import com.flipperdevices.info.impl.main.viewmodel.InfoViewModel

class InfoFragment : ComposeFragment() {
    private val viewModel by viewModels<InfoViewModel>()

    @Composable
    override fun RenderView() {
        val information by viewModel.getDeviceInformation().collectAsState()
        val connectionState by viewModel.getConnectionState().collectAsState()
        ComposeInfoScreen(information, connectionState)
    }
}
