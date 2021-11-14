package com.flipperdevices.deeplink.api

import com.flipperdevices.deeplink.model.Deeplink

/**
 * Each component which support deeplink should implement this interface
 * Don't forget register it via DeepLinkDispatcher#registerHandler
 */
interface DeepLinkHandler {
    /**
     * @return null if uri not supported
     */
    fun isSupportLink(link: Deeplink): DispatcherPriority?
    fun processLink(link: Deeplink)
}
