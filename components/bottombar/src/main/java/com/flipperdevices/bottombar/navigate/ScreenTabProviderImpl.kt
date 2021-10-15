package com.flipperdevices.bottombar.navigate

import com.flipperdevices.bottombar.main.TestFragment
import com.flipperdevices.bottombar.model.FlipperBottomTab
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.navigation.screen.InfoScreenProvider
import com.flipperdevices.filemanager.api.navigation.FileManagerScreenProvider
import com.flipperdevices.pair.api.PairComponentApi
import com.github.terrakok.cicerone.Screen
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class ScreenTabProviderImpl @Inject constructor(
    private val infoScreenProvider: InfoScreenProvider,
    private val fileManagerScreenProvider: FileManagerScreenProvider,
    private val pairComponentApi: PairComponentApi
) : ScreenTabProvider {
    override fun getScreen(tab: FlipperBottomTab): Screen {
        return when (tab) {
            FlipperBottomTab.DEVICE -> infoScreenProvider.deviceInformationScreen(pairComponentApi.getPairedDevice())
            FlipperBottomTab.STORAGE -> fileManagerScreenProvider.fileManager(
                pairComponentApi.getPairedDevice()
            )
            else -> FragmentScreen { TestFragment() }
        }
    }
}
