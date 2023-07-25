package com.flipperdevices.share.receive.api

import android.content.Intent
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.flipperdevices.archive.api.ArchiveFeatureEntry
import com.flipperdevices.bottombar.api.BottomNavigationHandleDeeplink
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.deeplink.api.DeepLinkHandler
import com.flipperdevices.deeplink.api.DispatcherPriority
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.deeplink.model.DeeplinkContent
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.SimpleEvent
import com.flipperdevices.share.api.KeyReceiveFeatureEntry
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
@ContributesMultibinding(AppGraph::class, DeepLinkHandler::class)
class ArchiveKeyDeeplinkHandlerImpl @Inject constructor(
    private val metricApi: MetricApi,
    private val archiveFeatureEntry: ArchiveFeatureEntry,
    private val keyReceiveFeatureEntry: KeyReceiveFeatureEntry,
    private val bottomHandleDeeplink: BottomNavigationHandleDeeplink
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

        val archiveIntent = Intent().apply {
            data = archiveFeatureEntry.getArchiveScreenByDeeplink().toUri()
        }

        val keyReceiveIntent = Intent().apply {
            data = keyReceiveFeatureEntry.getKeyReceiveScreenDeeplinkUrl(link).toUri()
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        bottomHandleDeeplink.handleDeepLink(archiveIntent)
        navController.handleDeepLink(keyReceiveIntent)
    }
}
