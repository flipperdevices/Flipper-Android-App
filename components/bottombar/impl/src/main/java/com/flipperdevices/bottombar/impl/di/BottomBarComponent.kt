package com.flipperdevices.bottombar.impl.di

import com.flipperdevices.bottombar.impl.main.BottomNavigationActivity
import com.flipperdevices.bottombar.impl.main.TabContainerFragment
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface BottomBarComponent {
    fun inject(activity: BottomNavigationActivity)
    fun inject(fragment: TabContainerFragment)
}
