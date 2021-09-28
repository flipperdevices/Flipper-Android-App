package com.flipper.bottombar.navigate

import com.flipper.bottombar.main.TestFragment
import com.flipper.bottombar.model.FlipperBottomTab
import com.flipper.core.di.AppGraph
import com.flipper.core.navigation.screen.InfoScreenProvider
import com.flipper.pair.api.PairComponentApi
import com.github.terrakok.cicerone.Screen
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class ScreenTabProviderImpl @Inject constructor(
    private val infoScreenProvider: InfoScreenProvider,
    private val pairComponentApi: PairComponentApi
) : ScreenTabProvider {
    override fun getScreen(tab: FlipperBottomTab): Screen {
        return when (tab) {
            FlipperBottomTab.DEVICE -> infoScreenProvider.deviceInformationScreen(pairComponentApi.getPairedDevice())
            else -> FragmentScreen { TestFragment() }
        }
    }
}
