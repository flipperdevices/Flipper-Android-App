package com.flipperdevices.bottombar.api

import com.flipperdevices.deeplink.model.Deeplink
import com.github.terrakok.cicerone.androidx.FragmentScreen

/**
 * Class which provide api to bottombar module
 */
interface BottomNavigationApi {
    fun getBottomNavigationFragment(deeplink: Deeplink? = null): FragmentScreen
}
