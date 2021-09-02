package com.flipper.pair.navigation

import com.flipper.core.di.AppGraph
import com.flipper.core.navigation.screen.PairScreenProvider
import com.flipper.pair.navigation.internal.PairNavigationScreens
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class PairScreenProvider @Inject constructor(
    private val pairNavigationScreens: PairNavigationScreens
) : PairScreenProvider {
    override fun startPairScreen() = pairNavigationScreens.permissionScreen()
}