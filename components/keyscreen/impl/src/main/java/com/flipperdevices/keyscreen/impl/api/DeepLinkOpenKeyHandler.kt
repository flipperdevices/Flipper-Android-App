package com.flipperdevices.keyscreen.impl.api

import android.content.Intent
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.flipperdevices.archive.api.ArchiveFeatureEntry
import com.flipperdevices.bottombar.api.BottomNavigationHandleDeeplink
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.deeplink.api.DeepLinkHandler
import com.flipperdevices.deeplink.api.DispatcherPriority
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.keyscreen.api.KeyScreenFeatureEntry
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject

@ContributesMultibinding(AppGraph::class, DeepLinkHandler::class)
class DeepLinkOpenKeyHandler @Inject constructor(
    private val archiveFeatureEntry: ArchiveFeatureEntry,
    private val keyScreenFeatureEntry: KeyScreenFeatureEntry,
    private val bottomHandleDeeplink: BottomNavigationHandleDeeplink
) : DeepLinkHandler {
    override fun isSupportLink(link: Deeplink): DispatcherPriority? {
        if (link is Deeplink.OpenKey) {
            return DispatcherPriority.HIGH
        }
        return null
    }

    override fun processLink(navController: NavController, link: Deeplink) {
        if (link !is Deeplink.OpenKey) return

        val archiveIntent = Intent().apply {
            data = archiveFeatureEntry.getArchiveScreenByDeeplink().toUri()
        }

        val keyReceiveIntent = Intent().apply {
            data = keyScreenFeatureEntry.getKeyScreenByDeeplink(link.keyPath).toUri()
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        bottomHandleDeeplink.handleDeepLink(archiveIntent)
        navController.handleDeepLink(keyReceiveIntent)
    }
}
