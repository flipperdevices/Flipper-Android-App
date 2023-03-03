package com.flipperdevices.widget.screen.deeplink

import android.content.Intent
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.deeplink.api.DeepLinkHandler
import com.flipperdevices.deeplink.api.DispatcherPriority
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.deeplink.model.DeeplinkConstants
import com.flipperdevices.widget.screen.api.DEEPLINK_WIDGET_URL
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject

@ContributesMultibinding(AppGraph::class, DeepLinkHandler::class)
class WidgetDeeplinkHandler @Inject constructor() : DeepLinkHandler {
    override fun isSupportLink(link: Deeplink): DispatcherPriority? {
        return when (link) {
            is Deeplink.WidgetOptions -> DispatcherPriority.HIGH
            else -> null
        }
    }

    override fun processLink(navController: NavController, link: Deeplink) {
        val url = DEEPLINK_WIDGET_URL.replace(
            oldValue = "{${DeeplinkConstants.KEY}}",
            newValue = link.serialization
        )

        val intent = Intent().apply {
            data = url.toUri()
        }

        navController.handleDeepLink(intent)
    }
}
