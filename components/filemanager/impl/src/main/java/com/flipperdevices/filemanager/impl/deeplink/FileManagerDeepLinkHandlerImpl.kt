package com.flipperdevices.filemanager.impl.deeplink

import android.net.Uri
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.deeplink.api.DispatcherPriority
import com.flipperdevices.filemanager.api.deeplink.FileManagerDeepLinkHandler
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, FileManagerDeepLinkHandler::class)
class FileManagerDeepLinkHandlerImpl @Inject constructor() : FileManagerDeepLinkHandler {
    override fun isSupportLink(uri: Uri): DispatcherPriority? {
        // val extension = uri.path?.substringAfterLast('.')
        return null
    }

    override fun processLink(uri: Uri) {
        TODO("Not yet implemented")
    }
}
