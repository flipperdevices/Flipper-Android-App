package com.flipperdevices.filemanager.upload.impl.deeplink

import android.content.Context
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.deeplink.model.DeeplinkContent
import com.flipperdevices.deeplink.model.openStream
import com.squareup.anvil.annotations.ContributesBinding
import okio.Source
import okio.source
import javax.inject.Inject

@ContributesBinding(AppGraph::class, DeeplinkContentProvider::class)
class AndroidDeeplinkContentProvider @Inject constructor(
    context: Context,
) : DeeplinkContentProvider {
    private val contentResolver = context.contentResolver

    override fun source(deeplinkContent: DeeplinkContent): Source? {
        return deeplinkContent.openStream(contentResolver)?.source()
    }
}
