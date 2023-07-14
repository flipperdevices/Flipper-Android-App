package com.flipperdevices.deeplink.impl.parser.delegates

import android.content.Context
import android.content.Intent
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.deeplink.api.DeepLinkParserDelegate
import com.flipperdevices.deeplink.impl.utils.Constants
import com.flipperdevices.deeplink.model.DeepLinkParserDelegatePriority
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.deeplink.model.DeeplinkContent
import com.flipperdevices.keyparser.api.KeyParser
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject

@ContributesMultibinding(AppGraph::class, DeepLinkParserDelegate::class)
class DeepLinkFlipperFormatCryproSharing @Inject constructor(
    private val keyParser: KeyParser
) :
    DeepLinkParserDelegate,
    LogTagProvider {
    override val TAG = "DeepLinkFlipperFormatSharing"

    override fun getPriority(
        context: Context,
        intent: Intent
    ): DeepLinkParserDelegatePriority? {
        if (intent.data == null) {
            return null
        }
        if (Constants.SUPPORTED_HOSTS.contains(intent.data?.host)) {
            return DeepLinkParserDelegatePriority.HIGH
        }
        return null
    }

    override suspend fun fromIntent(context: Context, intent: Intent): Deeplink? {
        val uri = intent.data ?: return null
        val flipperKeyCrypto = keyParser.parseUriToCryptoKeyData(uri) ?: return null

        val path = flipperKeyCrypto.pathToKey
        val flipperFilePath = FlipperFilePath(
            folder = path.substringBeforeLast("/"),
            nameWithExtension = path.substringAfterLast("/")
        )
        return Deeplink.FlipperKey(
            path = flipperFilePath,
            content = DeeplinkContent.FFFCryptoContent(key = flipperKeyCrypto)
        )
    }
}
