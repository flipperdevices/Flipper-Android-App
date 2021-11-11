package com.flipperdevices.deeplink.api

import android.net.Uri

interface DeepLinkDispatcher {
    /**
     * @return false if dispatcher not found handler for this uri
     */
    fun processUri(uri: Uri): Boolean

    fun registerHandler(deepLinkHandler: DeepLinkHandler)
}
