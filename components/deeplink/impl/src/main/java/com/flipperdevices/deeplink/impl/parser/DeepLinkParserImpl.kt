package com.flipperdevices.deeplink.impl.parser

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.android.toFullString
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.log.warn
import com.flipperdevices.deeplink.api.DeepLinkParser
import com.flipperdevices.deeplink.api.DeepLinkParserDelegate
import com.flipperdevices.deeplink.model.Deeplink
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, DeepLinkParser::class)
class DeepLinkParserImpl @Inject constructor(
    private val delegates: MutableSet<DeepLinkParserDelegate>
) : DeepLinkParser, LogTagProvider {
    override val TAG = "DeepLinkParser"

    override suspend fun fromUri(context: Context, uri: Uri): Deeplink? {
        info { "Try parse uri with scheme: ${uri.scheme}, uri: $uri" }

        return fromIntent(
            context,
            Intent().apply {
                data = uri
            }
        )
    }

    override suspend fun fromIntent(context: Context, intent: Intent): Deeplink? {
        info { "Try parse intent ${intent.toFullString()}" }

        val sortedDelegate = delegates.map { it to it.getPriority(context, intent) }
            .filter { (_, state) -> state != null }.sortedByDescending { (_, state) -> state }
            .map { (delegate, _) -> delegate }
        for (delegate in sortedDelegate) {
            try {
                info { "Try ${delegate.javaClass}..." }
                val deeplink = delegate.fromIntent(context, intent)
                if (deeplink != null) {
                    info { "Parsed deeplink: $deeplink. " }
                    return deeplink
                }
            } catch (e: Throwable) {
                error(e) { "Exception while try open ${intent.toFullString()}" }
            }
        }
        warn { "Failed parse intent ${intent.toFullString()}" }
        return null
    }
}
