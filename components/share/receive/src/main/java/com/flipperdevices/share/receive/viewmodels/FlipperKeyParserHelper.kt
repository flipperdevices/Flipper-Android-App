package com.flipperdevices.share.receive.viewmodels

import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.deeplink.model.DeeplinkContent

object FlipperKeyParserHelper {
    fun toFlipperKey(link: Deeplink?): FlipperKey? {
        if (link == null) return null
        return when (link.content) {
            is DeeplinkContent.FFFContent -> parseFFFContent(link)
            is DeeplinkContent.InternalStorageFile -> parseInternalFile(link)
            else -> null
        }
    }

    private fun parseFFFContent(link: Deeplink): FlipperKey? {
        val path = link.path ?: return null
        val deeplinkContent = link.content as? DeeplinkContent.FFFContent ?: return null
        return FlipperKey(path, deeplinkContent.content, synchronized = false)
    }

    private fun parseInternalFile(link: Deeplink): FlipperKey? {
        val deeplinkContent = link.content as? DeeplinkContent.InternalStorageFile ?: return null
        val fileKey = deeplinkContent.file
        return FlipperKey(
            path = FlipperKeyPath(
                folder = fileKey.extension,
                name = fileKey.name
            ),
            keyContent = FlipperKeyContent.InternalFile(fileKey),
            synchronized = false
        )
    }
}
