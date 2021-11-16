package com.flipperdevices.info.impl.main

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.fragment.app.viewModels
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.navigation.requireRouter
import com.flipperdevices.core.ui.ComposeFragment
import com.flipperdevices.filemanager.api.navigation.FileManagerScreenProvider
import com.flipperdevices.info.impl.di.InfoComponent
import com.flipperdevices.info.impl.main.compose.ComposeInfoScreen
import com.flipperdevices.info.impl.main.model.DeviceSubScreen
import com.flipperdevices.info.impl.main.viewmodel.InfoViewModel
import com.flipperdevices.pair.api.PairComponentApi
import com.flipperdevices.pair.api.PairScreenArgument
import com.flipperdevices.screenstreaming.api.ScreenStreamingApi
import javax.inject.Inject

class InfoFragment : ComposeFragment() {
    @Inject
    lateinit var pairComponentApi: PairComponentApi

    @Inject
    lateinit var fileManagerScreenProvider: FileManagerScreenProvider

    @Inject
    lateinit var screenStreaming: ScreenStreamingApi

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
        }, onOpenScreen = {
            val screen = when (it) {
                DeviceSubScreen.FILE_MANAGER -> fileManagerScreenProvider.fileManager()
                DeviceSubScreen.SCREEN_STREAMING -> screenStreaming.provideScreen()
            }
            requireRouter().navigateTo(screen)
        })
    }
}
