package com.flipperdevices.deeplink.impl.parser.delegates

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.android.filename
import com.flipperdevices.core.ktx.android.length
import com.flipperdevices.deeplink.api.DeepLinkParserDelegate
import com.flipperdevices.deeplink.model.DeepLinkParserDelegatePriority
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.deeplink.model.DeeplinkContent
import com.squareup.anvil.annotations.ContributesMultibinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ContributesMultibinding(AppGraph::class, DeepLinkParserDelegate::class)
class DeepLinkFileUriGrantPermission @Inject constructor() : DeepLinkParserDelegate {
    override fun getPriority(
        context: Context,
        intent: Intent
    ): DeepLinkParserDelegatePriority? {
        if (intent.data != null) {
            return DeepLinkParserDelegatePriority.DEFAULT
        }
        return null
    }

    override suspend fun fromIntent(context: Context, intent: Intent): Deeplink? {
        val uri = intent.data ?: return null

        val contentResolver = context.contentResolver

        // We need persistable permission for read file on next activities
        val permissionGranted = runCatching {
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }.isSuccess

        if (!permissionGranted) {
            return null
        }

        return Deeplink.RootLevel.SaveKey.ExternalContent(content = buildExternalUri(contentResolver, uri))
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
