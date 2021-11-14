package com.flipperdevices.singleactivity.api

import com.flipperdevices.deeplink.model.Deeplink

/**
 * Provide method for control single activity
 */
interface SingleActivityApi {
    /**
     * Open main screen
     */
    fun open(deeplink: Deeplink? = null)
}
