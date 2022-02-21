package com.flipperdevices.deeplink.impl.dispatcher

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.deeplink.api.DeepLinkDispatcher
import com.flipperdevices.deeplink.api.DeepLinkHandler
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.filemanager.api.share.FileManagerDeepLinkHandler
import com.flipperdevices.share.api.ArchiveKeyDeeplinkHandler
import com.github.terrakok.cicerone.Router
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class DeepLinkDispatcherImpl @Inject constructor(
    fileManagerHandler: FileManagerDeepLinkHandler,
    keyReceiveHandler: ArchiveKeyDeeplinkHandler
) : DeepLinkDispatcher {
    private val handlers = mutableListOf<DeepLinkHandler>(
        keyReceiveHandler,
        fileManagerHandler
    )

    override fun process(router: Router, deeplink: Deeplink): Boolean {
        val processHandler = handlers.map { it.isSupportLink(deeplink) to it }
            .filter { it.first != null }
            .maxByOrNull { it.first!! } ?: return false

        processHandler.second.processLink(router, deeplink)

        return true
    }

    override fun registerHandler(deepLinkHandler: DeepLinkHandler) {
        handlers.add(deepLinkHandler)
    }
}
