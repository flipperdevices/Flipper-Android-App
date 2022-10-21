package com.flipperdevices.deeplink.api

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.flipperdevices.deeplink.model.Deeplink

/**
 * Helper for build deeplink object from Intent
 */
interface DeepLinkParser {
    /**
     * Can be called only from permission owner for uri
     */
    @Throws(SecurityException::class)
    suspend fun fromUri(context: Context, uri: Uri): Deeplink?

    /**
     * Can be called only from permission owner for intent
     */
    @Throws(SecurityException::class)
    suspend fun fromIntent(context: Context, intent: Intent): Deeplink?
}
