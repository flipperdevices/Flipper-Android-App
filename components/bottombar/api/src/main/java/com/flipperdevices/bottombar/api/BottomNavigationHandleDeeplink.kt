package com.flipperdevices.bottombar.api

import androidx.compose.runtime.Immutable
import com.flipperdevices.bottombar.model.BottomBarTab

@Immutable
interface BottomNavigationHandleDeeplink {
    fun onChangeTab(
        tab: BottomBarTab,
        force: Boolean
    )
}
