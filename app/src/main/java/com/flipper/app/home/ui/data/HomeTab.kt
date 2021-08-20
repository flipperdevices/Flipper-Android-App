package com.flipper.app.home.ui.data

import com.flipper.app.R
import timber.log.Timber

enum class HomeTab(
    val menuItemId: Int
) {
    UserSpace(menuItemId = R.id.home_bottom_navigation_user_space),
    Extensions(menuItemId = R.id.home_bottom_navigation_extensions),
    Settings(menuItemId = R.id.home_bottom_navigation_settings);

    companion object {
        fun createFromMenuItemId(itemId: Int): HomeTab? {
            return values()
                .firstOrNull { tab -> tab.menuItemId == itemId }
                ?.also { Timber.e("unknown item id in home tabs: itemId=$itemId") }
        }
    }
}
