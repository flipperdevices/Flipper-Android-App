package com.flipperdevices.share.receive.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.deeplink.api.DeepLinkHandler
import com.flipperdevices.deeplink.api.DispatcherPriority
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.deeplink.model.DeeplinkContent
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.SimpleEvent
import com.flipperdevices.share.api.ArchiveKeyDeeplinkHandler
import com.flipperdevices.share.receive.fragments.KeyReceiveFragment
import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.androidx.FragmentScreen
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
            is DeeplinkContent.FFFSecureContent -> DispatcherPriority.DEFAULT
            is DeeplinkContent.FFFContent -> {
                if (link.path == null) return null
                return DispatcherPriority.DEFAULT
            }
            else -> null
        }
    }

    override fun processLink(router: Router, link: Deeplink) {
        val fragmentScreen = FragmentScreen { KeyReceiveFragment.newInstance(link) }
        metricApi.reportSimpleEvent(SimpleEvent.OPEN_SAVE_KEY)
        router.navigateTo(fragmentScreen)
    }
}
