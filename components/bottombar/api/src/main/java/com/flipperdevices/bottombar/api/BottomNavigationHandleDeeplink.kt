package com.flipperdevices.bottombar.api

import android.content.Intent
import com.flipperdevices.bottombar.model.BottomBarTab

interface BottomNavigationHandleDeeplink {
    fun handleDeepLink(intent: Intent)

    fun onChangeTab(
        tab: BottomBarTab,
        force: Boolean
    )
}
