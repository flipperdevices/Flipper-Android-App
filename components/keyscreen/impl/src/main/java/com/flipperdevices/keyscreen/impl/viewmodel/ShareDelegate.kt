package com.flipperdevices.keyscreen.impl.viewmodel

import android.content.Context
import android.content.Intent
import com.flipperdevices.bridge.dao.api.delegates.KeyParser
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ShareDelegate(
    private val context: Context,
    private val keyParser: KeyParser
) : LogTagProvider {
    override val TAG = "ShareDelegate"

    suspend fun share(flipperKey: FlipperKey) {
        val link = keyParser.keyToUrl(flipperKey)
        info { "Share ${flipperKey.path.nameWithExtension} with link $link" }
        shareLink(flipperKey, link)
    }

    private suspend fun shareLink(
        flipperKey: FlipperKey,
        link: String
    ) = withContext(Dispatchers.Main) {
        val intent = Intent(Intent.ACTION_SEND)

        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, link)
        intent.putExtra(Intent.EXTRA_TITLE, flipperKey.path.nameWithExtension)

        val chooserIntent = Intent.createChooser(intent, null)
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooserIntent)
    }
}
