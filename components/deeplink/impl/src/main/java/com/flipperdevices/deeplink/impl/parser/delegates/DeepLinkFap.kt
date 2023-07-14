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

private val SUPPORTED_HOSTS = listOf("lab.flipper.net")
private const val PATH = "apps"

@ContributesMultibinding(AppGraph::class, DeepLinkParserDelegate::class)
class DeepLinkFap @Inject constructor() : DeepLinkParserDelegate, LogTagProvider {
    override val TAG = "DeepLinkFap"

    override fun getPriority(
        context: Context,
        intent: Intent
    ): DeepLinkParserDelegatePriority? {
        if (intent.data == null) {
            return null
        }

        if (!SUPPORTED_HOSTS.contains(intent.data?.host)) {
            return null
        }

        val pathSegment = intent.data?.pathSegments
            ?: return null

        if (pathSegment.size == 2 && pathSegment.first() == PATH) {
            return DeepLinkParserDelegatePriority.HIGH
        }

        return null
    }

    override suspend fun fromIntent(context: Context, intent: Intent): Deeplink? {
        if (!SUPPORTED_HOSTS.contains(intent.data?.host)) {
            return null
        }
        val pathSegment = intent.data?.pathSegments ?: return null
        val fapId = pathSegment[1] ?: return null
        return Deeplink.Fap(fapId)
    }
}
