package com.flipper.app.home.ui.data

import com.flipper.app.R
import timber.log.Timber

enum class HomeTab(
    val menuItemId: Int
) {
    UserSpace(menuItemId = R.id.home_bottom_navigation_user_space),
    Market(menuItemId = R.id.home_bottom_navigation_market),
    Settings(menuItemId = R.id.home_bottom_navigation_settings);

    companion object {
        fun createFromMenuItemId(itemId: Int): HomeTab? {
            return when (itemId) {
                Market.menuItemId -> Market
                UserSpace.menuItemId -> UserSpace
                Settings.menuItemId -> Settings
                else -> {
                    Timber.e("unknown item id in home tabs: itemId=${itemId}")
                    null
                }
            }
        }
    }
}
