package com.flipperdevices.deeplink.impl.dispatcher

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.deeplink.api.DeepLinkDispatcher
import com.flipperdevices.deeplink.api.DeepLinkHandler
import com.flipperdevices.deeplink.model.Deeplink
import com.github.terrakok.cicerone.Router
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, DeepLinkDispatcher::class)
class DeepLinkDispatcherImpl @Inject constructor(
    private val handlers: MutableSet<DeepLinkHandler>
) : DeepLinkDispatcher, LogTagProvider {
    override val TAG = "DeepLinkDispatcher"

    override fun process(router: Router, deeplink: Deeplink): Boolean {
        val supportedHandlers = handlers.map { it.isSupportLink(deeplink) to it }
            .filter { it.first != null }

        info { "Found ${supportedHandlers.size} supported handlers: $supportedHandlers" }

        val processHandler = supportedHandlers
            .maxByOrNull { it.first!! } ?: return false

        info {
            "Choice handler ${processHandler.second.javaClass} " +
                "with priority ${processHandler.first}"
        }

        processHandler.second.processLink(router, deeplink)

        return true
    }
}
