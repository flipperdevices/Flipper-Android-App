package com.flipper.core.api

import android.content.Context

/**
 * Class which provide api to bottombar module
 */
interface BottomNavigationActivityApi {
    /**
     * Open main screen with bottom bar
     */
    fun openBottomNavigationScreen(context: Context)
}