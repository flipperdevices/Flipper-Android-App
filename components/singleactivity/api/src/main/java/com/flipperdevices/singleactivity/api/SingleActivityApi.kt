package com.flipperdevices.singleactivity.api

import android.content.Intent

/**
 * Provide method for control single activity
 */
interface SingleActivityApi {
    /**
     * Open main screen
     */
    fun open(launchParams: Intent? = null)
}
