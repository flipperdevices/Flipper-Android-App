package com.flipperdevices.deeplink.api

import android.net.Uri

/**
 * Each component which support deeplink should implement this interface
 * Don't forget register it via DeepLinkDispatcher#registerHandler
 */
interface DeepLinkHandler {
    /**
     * @return null if uri not supported
     */
    fun isSupportLink(uri: Uri): DispatcherPriority?
    fun processLink(uri: Uri)
}
