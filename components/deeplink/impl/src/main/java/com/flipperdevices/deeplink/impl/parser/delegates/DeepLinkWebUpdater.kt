package com.flipperdevices.deeplink.impl.parser.delegates

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.deeplink.api.DeepLinkParserDelegate
import com.flipperdevices.deeplink.model.DeepLinkParserDelegatePriority
import com.flipperdevices.deeplink.model.Deeplink
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject

private const val HOST = "my.flipp.dev"

@ContributesMultibinding(AppGraph::class, DeepLinkParserDelegate::class)
class DeepLinkWebUpdater @Inject constructor() : DeepLinkParserDelegate, LogTagProvider {
    override val TAG = "DeepLinkWebUpdater"

    override fun getPriority(
        context: Context,
        intent: Intent
    ): DeepLinkParserDelegatePriority {
        if (intent.data == null) {
            return DeepLinkParserDelegatePriority.LOW
        }
        if (intent.data?.host == HOST) {
            return DeepLinkParserDelegatePriority.HIGH
        }
        return DeepLinkParserDelegatePriority.LAST_CHANCE
    }

    override suspend fun fromIntent(context: Context, intent: Intent): Deeplink? {
        val uri = intent.data ?: return null
        val content = uriToContent(uri)
        val link = content.find { it.first == "url" }?.second ?: return null
        val version = content.find { it.first == "version" }?.second ?: ""
        val channel = content.find { it.first == "channel" }?.second ?: ""
        return Deeplink.WebUpdate(link, "$channel $version")
    }

    private fun uriToContent(uri: Uri): List<Pair<String, String>> {
        val query = uri.query ?: return listOf()
        return query.split("&").map {
            val (key, value) = it.split("=")
            key to value
        }
    }
}
