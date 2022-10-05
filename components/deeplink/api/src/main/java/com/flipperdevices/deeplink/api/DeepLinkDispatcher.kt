package com.flipperdevices.deeplink.api

import com.flipperdevices.deeplink.model.Deeplink
import com.github.terrakok.cicerone.Router

interface DeepLinkDispatcher {
    /**
     * @return false if dispatcher not found handler for this uri
     */
    fun process(router: Router, deeplink: Deeplink): Boolean
}
