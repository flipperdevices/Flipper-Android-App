package com.flipperdevices.bottombar.impl.navigate

import com.flipperdevices.bottombar.impl.model.FlipperBottomTab
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.filemanager.api.navigation.FileManagerScreenProvider
import com.flipperdevices.info.api.screen.InfoScreenProvider
import com.flipperdevices.screenstreaming.api.ScreenStreamingApi
import com.github.terrakok.cicerone.Screen
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class ScreenTabProviderImpl @Inject constructor(
    private val infoScreenProvider: InfoScreenProvider,
    private val fileManagerScreenProvider: FileManagerScreenProvider,
    private val screenStreamingApi: ScreenStreamingApi
) : ScreenTabProvider {
    override fun getScreen(tab: FlipperBottomTab): Screen {
        return when (tab) {
            FlipperBottomTab.DEVICE -> infoScreenProvider.deviceInformationScreen()
            FlipperBottomTab.STORAGE -> fileManagerScreenProvider.fileManager()
            FlipperBottomTab.SETTINGS -> screenStreamingApi.provideScreen()
        }
    }
}
