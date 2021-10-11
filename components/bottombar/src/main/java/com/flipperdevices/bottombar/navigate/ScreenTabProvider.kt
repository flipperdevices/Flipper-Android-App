package com.flipperdevices.bottombar.navigate

import com.flipperdevices.bottombar.model.FlipperBottomTab
import com.github.terrakok.cicerone.Screen

/**
 * Provide tab screens for bottom navigation activity and tab container
 */
interface ScreenTabProvider {
    /**
     * @return screen which associated with tab
     */
    fun getScreen(tab: FlipperBottomTab): Screen
}
