package com.flipper.bottombar.navigate

import com.flipper.bottombar.main.TestFragment
import com.flipper.bottombar.model.FlipperBottomTab
import com.flipper.core.di.AppGraph
import com.github.terrakok.cicerone.Screen
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject


@ContributesBinding(AppGraph::class)
class ScreenTabProviderImpl @Inject constructor() : ScreenTabProvider {
    override fun getScreen(tab: FlipperBottomTab): Screen {
        return FragmentScreen { TestFragment() }
    }
}