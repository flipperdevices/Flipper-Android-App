package com.flipperdevices.deeplink.impl.parser.delegates

import android.content.Context
import android.content.Intent
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.deeplink.api.DeepLinkParserDelegate
import com.flipperdevices.deeplink.model.DeepLinkParserDelegatePriority
import com.flipperdevices.deeplink.model.Deeplink
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject

private val SUPPORTED_HOSTS = listOf("lab.flipper.net", "my.flipp.dev")
private const val QUERY_URL = "url"
private const val QUERY_VERSION = "version"
private const val QUERY_CHANNEL = "channel"

@ContributesMultibinding(AppGraph::class, DeepLinkParserDelegate::class)
class DeepLinkWebUpdater @Inject constructor() : DeepLinkParserDelegate, LogTagProvider {
    override val TAG = "DeepLinkWebUpdater"

    override fun getPriority(
        context: Context,
        intent: Intent
    ): DeepLinkParserDelegatePriority? {
        if (intent.data == null) {
            return null
        }
        if (SUPPORTED_HOSTS.contains(intent.data?.host)) {
            return DeepLinkParserDelegatePriority.HIGH
        }
        return null
    }

    override suspend fun fromIntent(context: Context, intent: Intent): Deeplink? {
        val uri = intent.data ?: return null
        val link = uri.getQueryParameter(QUERY_URL) ?: return null
        val version = uri.getQueryParameter(QUERY_VERSION) ?: ""
        val channel = uri.getQueryParameter(QUERY_CHANNEL) ?: ""
        return Deeplink.WebUpdate(link, "$channel $version")
    }
}
