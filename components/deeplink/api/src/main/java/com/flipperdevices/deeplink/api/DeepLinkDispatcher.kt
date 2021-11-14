package com.flipperdevices.deeplink.api

import com.flipperdevices.deeplink.model.Deeplink

interface DeepLinkDispatcher {
    /**
     * @return false if dispatcher not found handler for this uri
     */
    fun process(deeplink: Deeplink): Boolean

    fun registerHandler(deepLinkHandler: DeepLinkHandler)
}
