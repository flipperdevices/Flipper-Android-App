package com.flipperdevices.filemanager.impl.deeplink

import android.content.ContentResolver
import android.net.Uri
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.navigation.global.CiceroneGlobal
import com.flipperdevices.deeplink.api.DispatcherPriority
import com.flipperdevices.filemanager.api.deeplink.FileManagerDeepLinkHandler
import com.flipperdevices.filemanager.api.navigation.FileManagerScreenProvider
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, FileManagerDeepLinkHandler::class)
class FileManagerDeepLinkHandlerImpl @Inject constructor(
    private val cicerone: CiceroneGlobal,
    private val fileManagerScreenProvider: FileManagerScreenProvider
) : FileManagerDeepLinkHandler {
    override fun isSupportLink(uri: Uri): DispatcherPriority? {
        val scheme = uri.scheme
        val isSupportedScheme = uri.path.isNullOrEmpty().not() &&
            (scheme == ContentResolver.SCHEME_CONTENT || scheme == ContentResolver.SCHEME_FILE)
        return if (isSupportedScheme) DispatcherPriority.LOW else null
    }

    override fun processLink(uri: Uri) {
        cicerone.getRouter().navigateTo(fileManagerScreenProvider.saveWithFileManager(uri))
    }
}
