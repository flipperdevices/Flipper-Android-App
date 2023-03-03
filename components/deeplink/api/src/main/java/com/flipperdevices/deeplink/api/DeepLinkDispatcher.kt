package com.flipperdevices.deeplink.api

import androidx.navigation.NavController
import com.flipperdevices.deeplink.model.Deeplink
import com.github.terrakok.cicerone.Router

interface DeepLinkDispatcher {
    /**
     * @return false if dispatcher not found handler for this uri
     */
    fun process(navController: NavController, deeplink: Deeplink): Boolean
}
