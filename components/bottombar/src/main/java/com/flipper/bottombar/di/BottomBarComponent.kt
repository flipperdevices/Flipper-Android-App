package com.flipper.bottombar.di

import com.flipper.bottombar.main.BottomNavigationActivity
import com.flipper.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface BottomBarComponent {
    fun inject(activity: BottomNavigationActivity)
}