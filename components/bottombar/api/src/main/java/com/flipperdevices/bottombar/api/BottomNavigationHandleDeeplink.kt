package com.flipperdevices.bottombar.api

import android.content.Intent
import androidx.compose.runtime.Immutable
import com.flipperdevices.bottombar.model.BottomBarTab

@Immutable
interface BottomNavigationHandleDeeplink {
    fun handleDeepLink(intent: Intent)

    fun onChangeTab(
        tab: BottomBarTab,
        force: Boolean
    )
}
