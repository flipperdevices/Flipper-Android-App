package com.flipperdevices.share.receive.api

import android.content.Intent
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.deeplink.api.DeepLinkHandler
import com.flipperdevices.deeplink.api.DispatcherPriority
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.deeplink.model.DeeplinkConstants
import com.flipperdevices.deeplink.model.DeeplinkContent
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.SimpleEvent
import com.flipperdevices.share.api.ArchiveKeyDeeplinkHandler
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
@ContributesMultibinding(AppGraph::class, DeepLinkHandler::class)
class ArchiveKeyDeeplinkHandlerImpl @Inject constructor(
    private val metricApi: MetricApi
) : ArchiveKeyDeeplinkHandler {
    override fun isSupportLink(link: Deeplink): DispatcherPriority? {
        if (link !is Deeplink.FlipperKey) {
            return null
        }
        return when (link.content) {
            is DeeplinkContent.InternalStorageFile -> DispatcherPriority.DEFAULT
            is DeeplinkContent.FFFCryptoContent -> DispatcherPriority.DEFAULT
            is DeeplinkContent.FFFContent -> {
                if (link.path == null) return null
                return DispatcherPriority.DEFAULT
            }
            else -> null
        }
    }

    override fun processLink(navController: NavController, link: Deeplink) {
        metricApi.reportSimpleEvent(SimpleEvent.OPEN_SAVE_KEY)

        val url = DEEPLINK_KEY_RECEIVE_URL.replace(
            oldValue = "{${DeeplinkConstants.KEY}}",
            newValue = link.serialization
        )

        val keyReceiveIntent = Intent().apply {
            data = url.toUri()
        }

        navController.handleDeepLink(keyReceiveIntent)
    }
}
