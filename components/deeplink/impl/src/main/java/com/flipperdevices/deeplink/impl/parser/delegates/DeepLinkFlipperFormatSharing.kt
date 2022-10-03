package com.flipperdevices.deeplink.impl.parser.delegates

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.flipperdevices.bridge.dao.api.delegates.KeyParser
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.deeplink.api.DeepLinkParserDelegate
import com.flipperdevices.deeplink.model.DeepLinkParserDelegatePriority
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.deeplink.model.DeeplinkContent
import com.squareup.anvil.annotations.ContributesMultibinding
import java.net.URLDecoder
import javax.inject.Inject

private const val SCHEME_FLIPPERKEY = "flipperkey"

@ContributesMultibinding(AppGraph::class, DeepLinkParserDelegate::class)
class DeepLinkFlipperFormatSharing @Inject constructor(
    private val parser: KeyParser
) : DeepLinkParserDelegate, LogTagProvider {
    override val TAG = "DeepLinkFlipperFormatSharing"

    override fun getPriority(
        context: Context,
        intent: Intent
    ): DeepLinkParserDelegatePriority {
        val uri = intent.data ?: return DeepLinkParserDelegatePriority.LOW
        return if (uri.scheme == SCHEME_FLIPPERKEY) {
            DeepLinkParserDelegatePriority.HIGH
        } else DeepLinkParserDelegatePriority.LOW
    }

    override suspend fun fromIntent(context: Context, intent: Intent): Deeplink? {
        var pureUri = intent.data ?: return null

        if (pureUri.scheme == SCHEME_FLIPPERKEY) {
            val query = pureUri.query
            val decodedQuery = URLDecoder.decode(query, "UTF-8")
            pureUri = Uri.parse(decodedQuery)
        }

        val (path, content) = parser.parseUri(pureUri) ?: return null
        return Deeplink(path, DeeplinkContent.FFFContent(path.nameWithExtension, content))
    }
}
