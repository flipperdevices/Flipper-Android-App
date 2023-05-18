package com.flipperdevices.deeplink.api

import androidx.navigation.NavController
import com.flipperdevices.deeplink.model.Deeplink

interface DeepLinkDispatcher {
    /**
     * @return false if dispatcher not found handler for this uri
     */
    suspend fun process(navController: NavController, deeplink: Deeplink): Boolean
}
