package com.flipperdevices.share.receive.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.deeplink.api.DispatcherPriority
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.deeplink.model.DeeplinkContent
import com.flipperdevices.share.api.ArchiveKeyDeeplinkHandler
import com.flipperdevices.share.receive.fragments.KeyReceiveFragment
import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class ArchiveKeyDeeplinkHandlerImpl @Inject constructor() : ArchiveKeyDeeplinkHandler {
    override fun isSupportLink(link: Deeplink): DispatcherPriority? {
        if (link.path == null || link.content == null) {
            return null
        }
        if (link.content is DeeplinkContent.FFFContent) {
            return DispatcherPriority.DEFAULT
        }
        return null
    }

    override fun processLink(router: Router, link: Deeplink) {
        val fragmentScreen = FragmentScreen { KeyReceiveFragment.newInstance(link) }
        router.navigateTo(fragmentScreen)
    }
}
