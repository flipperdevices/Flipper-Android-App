package com.flipperdevices.deeplink.impl.dispatcher

import android.net.Uri
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.deeplink.api.DeepLinkDispatcher
import com.flipperdevices.deeplink.api.DeepLinkHandler
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

    override fun processUri(uri: Uri): Boolean {
        val processHandler = handlers.map { it.isSupportLink(uri) to it }
            .filter { it.first != null }
            .maxByOrNull { it.first!! } ?: return false

        processHandler.second.processLink(uri)

        return true
    }

    override fun registerHandler(deepLinkHandler: DeepLinkHandler) {
        handlers.add(deepLinkHandler)
    }
}
