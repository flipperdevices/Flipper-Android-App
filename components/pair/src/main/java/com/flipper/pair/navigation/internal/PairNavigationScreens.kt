package com.flipper.pair.navigation.internal

import com.github.terrakok.cicerone.Screen

/**
 * List of module screens for internal usage
 */
interface PairNavigationScreens {
    /**
     * Screen provide logic for permission request
     */
    fun permissionScreen(): Screen

    /**
     * Screen provide ability select flipper device
     */
    fun findDeviceScreen(): Screen
}
