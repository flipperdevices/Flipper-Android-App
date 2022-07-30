package com.flipperdevices.deeplink.impl.parser.delegates

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.deeplink.impl.parser.filename
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.deeplink.model.DeeplinkContent
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeepLinkFileUriCopy : DeepLinkParserDelegate, LogTagProvider {
    override val TAG = "DeepLinkFileUriCopy"

    // Fallback if DeepLinkFileUriGrantPermission failed: copy from uri to tmp file
    override suspend fun fromUri(context: Context, uri: Uri): Deeplink {
        val contentResolver = context.contentResolver

        return Deeplink(
            content = buildInternalFile(
                contentResolver,
                context.cacheDir,
                uri
            )
        )
    }

    private suspend fun buildInternalFile(
        contentResolver: ContentResolver,
        cacheDir: File,
        uri: Uri
    ): DeeplinkContent? = withContext(Dispatchers.IO) {
        val filename = uri.filename(contentResolver) ?: System.currentTimeMillis().toString()
        val temporaryFile = File(cacheDir, filename)
        if (temporaryFile.exists()) {
            temporaryFile.delete()
        }
        val exception = runCatching {
            contentResolver.openInputStream(uri).use { inputStream ->
                temporaryFile.outputStream().use { outputStream ->
                    inputStream?.copyTo(outputStream)
                }
            }
        }.exceptionOrNull()

        if (exception != null) {
            error(exception) { "Error while copy uri $uri to internal file $temporaryFile" }
            return@withContext null
        }

        return@withContext DeeplinkContent.InternalStorageFile(temporaryFile.absolutePath)
    }
}
