package com.flipper.core.navigation.screen

import com.github.terrakok.cicerone.Screen

/**
 * Provide screens for pair components
 */
interface PairScreenProvider {
    fun startPairScreen(): Screen
}
