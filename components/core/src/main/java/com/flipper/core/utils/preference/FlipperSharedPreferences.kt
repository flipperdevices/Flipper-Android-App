package com.flipper.core.utils.preference

import android.content.SharedPreferences

/**
 * I want add some methods in default shared preference,
 * so i create this class
 */
interface FlipperSharedPreferences : SharedPreferences {
    /**
     * We warm up shared preference before usage.
     * Just read and parse sharedpreferences file for fast access to object after
     */
    fun warmUp()
}
