package com.flipperdevices.filemanager.impl.deeplink

import android.content.ContentResolver
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.deeplink.api.DispatcherPriority
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.deeplink.model.DeeplinkContent
import com.flipperdevices.filemanager.api.share.FileManagerDeepLinkHandler
import com.github.terrakok.cicerone.Router
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, FileManagerDeepLinkHandler::class)
class FileManagerDeepLinkHandlerImpl @Inject constructor() : FileManagerDeepLinkHandler {
    override fun isSupportLink(link: Deeplink): DispatcherPriority? {
        val content = link.content ?: return null
        return when (content) {
            is DeeplinkContent.InternalStorageFile -> {
                DispatcherPriority.LOW
            }
            is DeeplinkContent.ExternalUri -> {
                val scheme = content.uri.scheme
                val isSupportedScheme = scheme == ContentResolver.SCHEME_CONTENT ||
                    scheme == ContentResolver.SCHEME_FILE
                val isNotNullPath = content.uri.path.isNullOrEmpty().not()

                if (isSupportedScheme && isNotNullPath) DispatcherPriority.LOW else null
            }
            is DeeplinkContent.FFFContent -> DispatcherPriority.LOW
        }
    }

    override fun processLink(router: Router, link: Deeplink) {
        val content = link.content ?: error("You can't process link here without content")
        // TODO router.navigateTo(fileManagerScreenProvider.saveWithFileManager(content))
    }
}
