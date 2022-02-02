package com.flipperdevices.deeplink.api

import android.app.Activity
import android.net.Uri
import com.flipperdevices.deeplink.model.DeeplinkContent

/**
 * Helper for build deeplink object from some objects
 */
interface DeepLinkContentProvider {
    /**
     * Can be called only from permission owner for uri
     */
    @Throws(SecurityException::class)
    suspend fun fromUri(activity: Activity, uri: Uri): DeeplinkContent?
}
