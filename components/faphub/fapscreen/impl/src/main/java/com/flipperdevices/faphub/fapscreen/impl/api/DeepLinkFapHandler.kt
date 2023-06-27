package com.flipperdevices.faphub.fapscreen.impl.api

import android.content.Intent
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.flipperdevices.bottombar.api.BottomNavigationHandleDeeplink
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.deeplink.api.DeepLinkHandler
import com.flipperdevices.deeplink.api.DispatcherPriority
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.faphub.fapscreen.api.FapScreenApi
import com.flipperdevices.faphub.main.api.FapHubHandleDeeplink
import com.flipperdevices.hub.api.HubFeatureEntry
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject

@ContributesMultibinding(AppGraph::class, DeepLinkHandler::class)
class DeepLinkFapHandler @Inject constructor(
    private val hubFeatureEntry: HubFeatureEntry,
    private val bottomHandleDeeplink: BottomNavigationHandleDeeplink,
    private val fapScreenApi: FapScreenApi,
    private val fapHubHandleDeeplink: FapHubHandleDeeplink
) : DeepLinkHandler {
    override fun isSupportLink(link: Deeplink): DispatcherPriority? {
        if (link is Deeplink.Fap) {
            return DispatcherPriority.HIGH
        }
        return null
    }

    override fun processLink(navController: NavController, link: Deeplink) {
        if (link !is Deeplink.Fap) return

        val hubIntent = Intent().apply {
            data = hubFeatureEntry.getHubScreenByDeeplink().toUri()
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val fapScreenIntent = Intent().apply {
            data = fapScreenApi.getFapScreenByDeeplink(link.appId).toUri()
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        bottomHandleDeeplink.handleDeepLink(hubIntent)
        fapHubHandleDeeplink.handleDeepLink(fapScreenIntent)
    }
}
