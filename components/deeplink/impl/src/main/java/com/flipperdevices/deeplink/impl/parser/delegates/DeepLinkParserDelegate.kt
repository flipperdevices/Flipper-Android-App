package com.flipperdevices.deeplink.impl.parser.delegates

import android.app.Activity
import android.net.Uri
import com.flipperdevices.deeplink.model.Deeplink

interface DeepLinkParserDelegate {
    suspend fun fromUri(activity: Activity, uri: Uri): Deeplink?
}
