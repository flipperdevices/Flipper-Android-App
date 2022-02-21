package com.flipperdevices.deeplink.impl.parser.delegates

import android.app.Activity
import android.net.Uri
import com.flipperdevices.bridge.dao.api.delegates.KeyParser
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.deeplink.impl.di.DeepLinkComponent
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.deeplink.model.DeeplinkContent
import javax.inject.Inject

class DeepLinkFlipperFormatSharing : DeepLinkParserDelegate, LogTagProvider {
    override val TAG = "DeepLinkFlipperFormatSharing"

    @Inject
    lateinit var parser: KeyParser

    init {
        ComponentHolder.component<DeepLinkComponent>().inject(this)
    }

    override suspend fun fromUri(activity: Activity, uri: Uri): Deeplink? {
        val (path, content) = parser.parseUri(uri) ?: return null
        return Deeplink(path, DeeplinkContent.FFFContent(path.name, content))
    }
}
