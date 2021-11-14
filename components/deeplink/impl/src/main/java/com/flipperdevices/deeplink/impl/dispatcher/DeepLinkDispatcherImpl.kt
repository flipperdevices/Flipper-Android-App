package com.flipperdevices.deeplink.impl.dispatcher

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.deeplink.api.DeepLinkDispatcher
import com.flipperdevices.deeplink.api.DeepLinkHandler
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.filemanager.api.deeplink.FileManagerDeepLinkHandler
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ContributesBinding(AppGraph::class)
class DeepLinkDispatcherImpl @Inject constructor(
    fileManagerHandler: FileManagerDeepLinkHandler
) : DeepLinkDispatcher {
    private val handlers = mutableListOf<DeepLinkHandler>(fileManagerHandler)

    override fun process(deeplink: Deeplink): Boolean {
        val processHandler = handlers.map { it.isSupportLink(deeplink) to it }
            .filter { it.first != null }
            .maxByOrNull { it.first!! } ?: return false

        processHandler.second.processLink(deeplink)

        return true
    }

    override fun registerHandler(deepLinkHandler: DeepLinkHandler) {
        handlers.add(deepLinkHandler)
    }
}
