package com.flipperdevices.deeplink.api

import androidx.navigation.NavController
import com.flipperdevices.deeplink.model.Deeplink
import com.github.terrakok.cicerone.Router

/**
 * Each component which support deeplink should implement this interface
 * Don't forget register it via DeepLinkDispatcher#registerHandler
 */
interface DeepLinkHandler {
    /**
     * @return null if uri not supported
     */
    fun isSupportLink(link: Deeplink): DispatcherPriority?
    fun processLink(navController: NavController, link: Deeplink)
}
