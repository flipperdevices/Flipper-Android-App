package com.flipperdevices.info.impl.api

import android.content.Intent
import androidx.navigation.NavController
import com.flipperdevices.bottombar.api.BottomNavigationHandleDeeplink
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.deeplink.api.DeepLinkHandler
import com.flipperdevices.deeplink.api.DispatcherPriority
import com.flipperdevices.deeplink.model.Deeplink
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject

@ContributesMultibinding(AppGraph::class, DeepLinkHandler::class)
class InfoDeeplinkHandler @Inject constructor(
    private val bottomHandleDeeplink: BottomNavigationHandleDeeplink
) : DeepLinkHandler {
    override fun isSupportLink(link: Deeplink): DispatcherPriority? {
        return when (link) {
            is Deeplink.WebUpdate -> DispatcherPriority.DEFAULT
            is Deeplink.OpenUpdate -> DispatcherPriority.DEFAULT
            else -> null
        }
    }

    override fun processLink(navController: NavController, link: Deeplink) {
        val intent = Intent().apply {
            // data = infoFeatureEntry.getWebUpdateByDeeplink(link).toUri()
        }
        bottomHandleDeeplink.handleDeepLink(intent)
    }
}
