package com.flipperdevices.deeplink.impl.parser

import android.content.Context
import android.net.Uri
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.deeplink.api.DeepLinkParser
import com.flipperdevices.deeplink.impl.parser.delegates.DeepLinkFileUriCopy
import com.flipperdevices.deeplink.impl.parser.delegates.DeepLinkFileUriGrantPermission
import com.flipperdevices.deeplink.impl.parser.delegates.DeepLinkFlipperFormatSharing
import com.flipperdevices.deeplink.impl.parser.delegates.DeepLinkParserDelegate
import com.flipperdevices.deeplink.model.Deeplink
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, DeepLinkParser::class)
class DeepLinkParserImpl @Inject constructor() : DeepLinkParser, LogTagProvider {
    override val TAG = "DeepLinkParserImpl"

    private val delegates: List<DeepLinkParserDelegate> = listOf(
        DeepLinkFlipperFormatSharing(),
        DeepLinkFileUriGrantPermission(),
        DeepLinkFileUriCopy()
    )

    @Suppress("TooGenericExceptionCaught")
    override suspend fun fromUri(context: Context, uri: Uri): Deeplink? {
        info { "Try parse uri with scheme: ${uri.scheme}, uri: $uri" }

        for (delegate in delegates) {
            try {
                info { "Try ${delegate.javaClass}..." }
                val deeplink = delegate.fromUri(context, uri)
                if (deeplink != null) {
                    return deeplink
                }
            } catch (e: Throwable) {
                error(e) { "Exception while try parse $uri with $delegate" }
            }
        }
        return null
    }
}
