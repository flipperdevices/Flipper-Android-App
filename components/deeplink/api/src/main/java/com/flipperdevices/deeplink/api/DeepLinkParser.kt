package com.flipperdevices.deeplink.api

import android.content.Context
import android.net.Uri
import com.flipperdevices.deeplink.model.Deeplink

/**
 * Helper for build deeplink object from URI
 */
interface DeepLinkParser {
    /**
     * Can be called only from permission owner for uri
     *
     * @return golang style, first return argument is a deeplink
     * and second is a throwable if deeplink is null
     */
    @Throws(SecurityException::class)
    suspend fun fromUri(context: Context, uri: Uri): Deeplink?
}
