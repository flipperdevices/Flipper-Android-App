package com.flipperdevices.keyscreen.impl.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.deeplink.api.DeepLinkHandler
import com.flipperdevices.deeplink.api.DispatcherPriority
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.keyscreen.api.KeyScreenApi
import com.github.terrakok.cicerone.Router
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject

@ContributesMultibinding(AppGraph::class, DeepLinkHandler::class)
class DeepLinkOpenKeyHandler @Inject constructor(
    private val keyScreenApi: KeyScreenApi
) : DeepLinkHandler {
    override fun isSupportLink(link: Deeplink): DispatcherPriority? {
        if (link is Deeplink.OpenKey) {
            return DispatcherPriority.HIGH
        }
        return null
    }

    override fun processLink(router: Router, link: Deeplink) {
        if (link is Deeplink.OpenKey) {
            router.navigateTo(keyScreenApi.getKeyScreenScreen(link.keyPath))
        }
    }
}