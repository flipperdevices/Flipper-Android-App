package com.flipperdevices.widget.screen.deeplink

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.deeplink.api.DeepLinkHandler
import com.flipperdevices.deeplink.api.DispatcherPriority
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.widget.api.WidgetScreenApi
import com.github.terrakok.cicerone.Router
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject

@ContributesMultibinding(AppGraph::class, DeepLinkHandler::class)
class WidgetDeeplinkHandler @Inject constructor(
    private val widgetScreenApi: WidgetScreenApi
) : DeepLinkHandler {
    override fun isSupportLink(link: Deeplink): DispatcherPriority? {
        return if (link is Deeplink.WidgetOptions) {
            DispatcherPriority.HIGH
        } else {
            null
        }
    }

    override fun processLink(router: Router, link: Deeplink) {
        val widgetOptionsLink = link as? Deeplink.WidgetOptions ?: return
        router.navigateTo(widgetScreenApi.getWidgetOptionsScreen(widgetOptionsLink.appWidgetId))
    }
}
