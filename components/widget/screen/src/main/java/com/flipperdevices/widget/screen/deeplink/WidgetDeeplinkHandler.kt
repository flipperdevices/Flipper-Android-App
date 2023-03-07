package com.flipperdevices.widget.screen.deeplink

import android.content.Intent
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.deeplink.api.DeepLinkHandler
import com.flipperdevices.deeplink.api.DispatcherPriority
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.widget.api.WidgetFeatureEntry
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject

@ContributesMultibinding(AppGraph::class, DeepLinkHandler::class)
class WidgetDeeplinkHandler @Inject constructor(
    private val widgetFeatureEntry: WidgetFeatureEntry
) : DeepLinkHandler {
    override fun isSupportLink(link: Deeplink): DispatcherPriority? {
        return when (link) {
            is Deeplink.WidgetOptions -> DispatcherPriority.HIGH
            else -> null
        }
    }

    override fun processLink(navController: NavController, link: Deeplink) {
        val intent = Intent().apply {
            data = widgetFeatureEntry.getWidgetScreenByDeeplink(link).toUri()
        }
        navController.handleDeepLink(intent)
    }
}
