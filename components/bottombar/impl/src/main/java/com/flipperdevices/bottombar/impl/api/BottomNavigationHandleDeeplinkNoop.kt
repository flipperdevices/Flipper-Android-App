package com.flipperdevices.bottombar.impl.api

import com.flipperdevices.bottombar.api.BottomNavigationHandleDeeplink
import com.flipperdevices.bottombar.model.BottomBarTab
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, BottomNavigationHandleDeeplink::class)
class BottomNavigationHandleDeeplinkNoop @Inject constructor() : BottomNavigationHandleDeeplink {
    override fun onChangeTab(tab: BottomBarTab, force: Boolean) = Unit
}
