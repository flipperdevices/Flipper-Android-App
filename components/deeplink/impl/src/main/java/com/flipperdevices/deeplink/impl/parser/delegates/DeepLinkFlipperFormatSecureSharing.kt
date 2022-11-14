package com.flipperdevices.deeplink.impl.parser.delegates

import android.content.Context
import android.content.Intent
import com.flipperdevices.bridge.dao.api.QUERY_DELIMITED_CHAR
import com.flipperdevices.bridge.dao.api.QUERY_VALUE_DELIMITED_CHAR
import com.flipperdevices.bridge.dao.api.SUPPORTED_HOSTS
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.deeplink.api.DeepLinkParserDelegate
import com.flipperdevices.deeplink.model.DeepLinkParserDelegatePriority
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.deeplink.model.DeeplinkContent
import com.squareup.anvil.annotations.ContributesMultibinding
import java.net.URLDecoder
import javax.inject.Inject

private const val QUERY_PATH = "path"
private const val QUERY_KEY = "key"

@ContributesMultibinding(AppGraph::class, DeepLinkParserDelegate::class)
class DeepLinkFlipperFormatSecureSharing @Inject constructor() :
    DeepLinkParserDelegate,
    LogTagProvider {
    override val TAG = "DeepLinkFlipperFormatSharing"

    override fun getPriority(
        context: Context,
        intent: Intent
    ): DeepLinkParserDelegatePriority {
        if (intent.data == null) {
            return DeepLinkParserDelegatePriority.LOW
        }
        if (SUPPORTED_HOSTS.contains(intent.data?.host)) {
            return DeepLinkParserDelegatePriority.HIGH
        }
        return DeepLinkParserDelegatePriority.LAST_CHANCE
    }

    override suspend fun fromIntent(context: Context, intent: Intent): Deeplink? {
        val uri = intent.data ?: return null
        val fragment = uri.fragment ?: return null
        val decodedFragment = URLDecoder.decode(fragment, "UTF-8")
        val parsedContentPairs = decodedFragment.split(QUERY_DELIMITED_CHAR).map {
            it.substringBefore(QUERY_VALUE_DELIMITED_CHAR) to it.substringAfter(
                QUERY_VALUE_DELIMITED_CHAR
            )
        }
        val path = parsedContentPairs
            .firstOrNull { it.first == QUERY_PATH }
            ?.second
            ?: return null

        val key = parsedContentPairs
            .firstOrNull { it.first == QUERY_KEY }
            ?.second
            ?: return null
        val fileId = uri.pathSegments.last() ?: return null
        return Deeplink.FlipperKey(
            path = null,
            content = DeeplinkContent.FFFSecureContent(
                filePath = path,
                key = key,
                fileId = fileId
            )
        )
    }
}
