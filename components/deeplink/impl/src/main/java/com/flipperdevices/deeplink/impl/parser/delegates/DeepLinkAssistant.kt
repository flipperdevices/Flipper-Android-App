package com.flipperdevices.deeplink.impl.parser.delegates

import android.content.Context
import android.net.Uri
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.deeplink.model.Deeplink

class DeepLinkAssistant : DeepLinkParserDelegate, LogTagProvider {
    override val TAG = "DeepLinkAssistant"

    override suspend fun fromUri(context: Context, uri: Uri): Deeplink? {
        info { "Google Assistant command with uri: $uri" }
        return null
    }
}
