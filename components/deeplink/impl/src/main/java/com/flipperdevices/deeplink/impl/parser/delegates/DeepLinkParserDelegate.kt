package com.flipperdevices.deeplink.impl.parser.delegates

import android.content.Context
import android.net.Uri
import com.flipperdevices.deeplink.model.Deeplink

interface DeepLinkParserDelegate {
    suspend fun fromUri(context: Context, uri: Uri): Deeplink?
}
