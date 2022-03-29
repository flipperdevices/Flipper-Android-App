package com.flipperdevices.bottombar.impl.di

import com.flipperdevices.bottombar.impl.main.BottomNavigationFragment
import com.flipperdevices.bottombar.impl.main.TabContainerFragment
import com.flipperdevices.bottombar.impl.main.service.BottomNavigationViewModel
import com.flipperdevices.bottombar.impl.main.viewmodel.InAppNotificationViewModel
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface BottomBarComponent {
    fun inject(fragment: BottomNavigationFragment)
    fun inject(fragment: TabContainerFragment)
    fun inject(viewModel: InAppNotificationViewModel)
    fun inject(viewModel: BottomNavigationViewModel)
}
