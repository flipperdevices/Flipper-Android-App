package com.flipperdevices.keyscreen.impl.api

import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.deeplink.api.DeepLinkHandler
import com.flipperdevices.deeplink.api.DispatcherPriority
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.keyscreen.api.KeyScreenApi
import com.github.terrakok.cicerone.Router
import com.squareup.anvil.annotations.ContributesMultibinding
import java.net.URL
import javax.inject.Inject
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@ContributesMultibinding(AppGraph::class, DeepLinkHandler::class)
class DeepLinkOpenKeyHandler @Inject constructor() : DeepLinkHandler {
    override fun isSupportLink(link: Deeplink): DispatcherPriority? {
        if (link is Deeplink.OpenKey) {
            return DispatcherPriority.HIGH
        }
        return null
    }

    override fun processLink(navController: NavController, link: Deeplink) {
        if (link !is Deeplink.OpenKey) return

        val keyPath = link.keyPath
        val url = DEEPLINK_FLIPPER_KEY_URL.replace(
            oldValue = "{$EXTRA_KEY_PATH}",
            newValue = Uri.encode(Json.encodeToString(keyPath))
        )

        val intent = Intent().apply {
            data = url.toUri()
        }

        navController.handleDeepLink(intent)
    }
}
