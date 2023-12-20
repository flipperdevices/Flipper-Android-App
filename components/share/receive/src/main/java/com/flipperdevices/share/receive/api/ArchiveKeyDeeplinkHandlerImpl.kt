package com.flipperdevices.share.receive.api

import androidx.navigation.NavController
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.deeplink.api.DeepLinkHandler
import com.flipperdevices.deeplink.api.DispatcherPriority
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.deeplink.model.DeeplinkContent
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.SimpleEvent
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
@ContributesMultibinding(AppGraph::class, DeepLinkHandler::class)
class ArchiveKeyDeeplinkHandlerImpl @Inject constructor(
    private val metricApi: MetricApi
) : DeepLinkHandler {
    override fun isSupportLink(link: Deeplink): DispatcherPriority? {
        var content: DeeplinkContent? = null
        var path: FlipperFilePath? = null
        if (link is Deeplink.ExternalContent) {
            content = link.content
        }
        if (link is Deeplink.FlipperKey) {
            path = link.path
            content = link.content
        }

        return when (content) {
            is DeeplinkContent.InternalStorageFile -> DispatcherPriority.DEFAULT
            is DeeplinkContent.FFFCryptoContent -> DispatcherPriority.DEFAULT
            is DeeplinkContent.FFFContent -> {
                if (path == null) return null
                return DispatcherPriority.DEFAULT
            }

            else -> null
        }
    }

    override fun processLink(navController: NavController, link: Deeplink) {
        metricApi.reportSimpleEvent(SimpleEvent.OPEN_SAVE_KEY)
    }
}
