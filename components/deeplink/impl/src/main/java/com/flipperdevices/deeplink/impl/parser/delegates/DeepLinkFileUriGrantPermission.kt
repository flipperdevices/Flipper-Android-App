package com.flipperdevices.deeplink.impl.parser.delegates

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.flipperdevices.deeplink.impl.parser.filename
import com.flipperdevices.deeplink.impl.parser.length
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.deeplink.model.DeeplinkContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeepLinkFileUriGrantPermission : DeepLinkParserDelegate {
    override suspend fun fromUri(context: Context, uri: Uri): Deeplink? {
        val contentResolver = context.contentResolver

        // We need persistable permission for read file on next activities
        val permissionGranted = runCatching {
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }.isSuccess

        if (!permissionGranted) {
            return null
        }

        return Deeplink(content = buildExternalUri(contentResolver, uri))
    }

    private suspend fun buildExternalUri(
        contentResolver: ContentResolver,
        uri: Uri
    ): DeeplinkContent = withContext(Dispatchers.IO) {
        return@withContext DeeplinkContent.ExternalUri(
            filename = uri.filename(contentResolver),
            size = uri.length(contentResolver),
            uriString = uri.toString()
        )
    }
}
