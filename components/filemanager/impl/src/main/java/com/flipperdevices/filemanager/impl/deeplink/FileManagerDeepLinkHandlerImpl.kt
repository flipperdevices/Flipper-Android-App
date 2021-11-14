package com.flipperdevices.filemanager.impl.deeplink

import android.content.ContentResolver
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.navigation.global.CiceroneGlobal
import com.flipperdevices.deeplink.api.DispatcherPriority
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.deeplink.model.DeeplinkContent
import com.flipperdevices.filemanager.api.deeplink.FileManagerDeepLinkHandler
import com.flipperdevices.filemanager.api.navigation.FileManagerScreenProvider
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, FileManagerDeepLinkHandler::class)
class FileManagerDeepLinkHandlerImpl @Inject constructor(
    private val cicerone: CiceroneGlobal,
    private val fileManagerScreenProvider: FileManagerScreenProvider
) : FileManagerDeepLinkHandler {
    override fun isSupportLink(link: Deeplink): DispatcherPriority? {
        val content = link.content ?: return null
        return when (content) {
            is DeeplinkContent.InternalStorageFile -> {
                DispatcherPriority.LOW
            }
            is DeeplinkContent.ExternalUri -> {
                val scheme = content.uri.scheme
                val isSupportedScheme = content.uri.path.isNullOrEmpty().not() &&
                    (scheme == ContentResolver.SCHEME_CONTENT || scheme == ContentResolver.SCHEME_FILE)

                if (isSupportedScheme) DispatcherPriority.LOW else null
            }
        }
    }

    override fun processLink(link: Deeplink) {
        val content = link.content ?: error("You can't process link here without content")
        cicerone.getRouter().navigateTo(fileManagerScreenProvider.saveWithFileManager(content))
    }
}
