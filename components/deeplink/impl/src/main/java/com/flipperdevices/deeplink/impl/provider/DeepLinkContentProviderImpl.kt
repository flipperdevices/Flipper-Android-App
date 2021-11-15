package com.flipperdevices.deeplink.impl.provider

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.deeplink.api.DeepLinkContentProvider
import com.flipperdevices.deeplink.model.DeeplinkContent
import com.squareup.anvil.annotations.ContributesBinding
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@ContributesBinding(AppGraph::class, DeepLinkContentProvider::class)
class DeepLinkContentProviderImpl @Inject constructor() : DeepLinkContentProvider, LogTagProvider {
    override val TAG = "DeepLinkContentProvider"

    override suspend fun fromUri(activity: Activity, uri: Uri): DeeplinkContent? {
        val contentResolver = activity.contentResolver
        // We need persistable permission for read file on next activities
        val permissionGranted = runCatching {
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }.isSuccess
        if (permissionGranted) {
            return buildExternalUri(contentResolver, uri)
        }
        // Fallback: copy from uri to tmp file
        return buildInternalFile(contentResolver, activity.cacheDir, uri)
    }

    private suspend fun buildExternalUri(
        contentResolver: ContentResolver,
        uri: Uri
    ): DeeplinkContent = withContext(Dispatchers.IO) {
        return@withContext DeeplinkContent.ExternalUri(
            filename = uri.filename(contentResolver),
            size = uri.length(contentResolver),
            uri = uri
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

        return@withContext DeeplinkContent.InternalStorageFile(temporaryFile)
    }
}
