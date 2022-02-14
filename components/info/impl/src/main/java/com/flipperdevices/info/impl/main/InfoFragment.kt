package com.flipperdevices.info.impl.main

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.fragment.app.viewModels
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.navigation.global.CiceroneGlobal
import com.flipperdevices.core.navigation.requireRouter
import com.flipperdevices.core.ui.ComposeFragment
import com.flipperdevices.debug.api.DebugScreenApi
import com.flipperdevices.filemanager.api.navigation.FileManagerScreenProvider
import com.flipperdevices.firstpair.api.FirstPairApi
import com.flipperdevices.info.impl.di.InfoComponent
import com.flipperdevices.info.impl.main.compose.ComposeInfoScreen
import com.flipperdevices.info.impl.main.model.DeviceSubScreen
import com.flipperdevices.info.impl.main.viewmodel.InfoViewModel
import com.flipperdevices.screenstreaming.api.ScreenStreamingApi
import javax.inject.Inject

class InfoFragment : ComposeFragment() {
    @Inject
    lateinit var firstPairApi: FirstPairApi

    @Inject
    lateinit var ciceroneGlobal: CiceroneGlobal

    @Inject
    lateinit var fileManagerScreenProvider: FileManagerScreenProvider

    @Inject
    lateinit var debugScreenProvider: DebugScreenApi

    @Inject
    lateinit var screenStreaming: ScreenStreamingApi

    private val viewModel by viewModels<InfoViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ComponentHolder.component<InfoComponent>().inject(this)
    }

    @Composable
    override fun RenderView() {
        val information by viewModel.getDeviceInformation().collectAsState()
        val connectionState by viewModel.getConnectionState().collectAsState()
        ComposeInfoScreen(information, connectionState, connectionToAnotherDeviceButton = {
            ciceroneGlobal.getRouter().navigateTo(firstPairApi.getFirstPairScreen())
        }, onOpenScreen = {
                val screen = when (it) {
                    DeviceSubScreen.DEBUG -> debugScreenProvider.getDebugScreen()
                    DeviceSubScreen.FILE_MANAGER -> fileManagerScreenProvider.fileManager()
                    DeviceSubScreen.SCREEN_STREAMING -> screenStreaming.provideScreen()
                }
                requireRouter().navigateTo(screen)
            })
    }
}
