package com.flipperdevices.core.navigation.global

import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.Router

/**
 * Highest-level router
 *
 * For navigation in tab, please, use FragmentNavigationKtx#requireRouter
 */
interface CiceroneGlobal {
    fun getRouter(): Router
    fun getNavigationHolder(): NavigatorHolder
}
