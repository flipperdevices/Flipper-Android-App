package com.flipperdevices.filemanager.upload.impl.deeplink

import android.content.Context
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.deeplink.model.DeeplinkContent
import com.flipperdevices.deeplink.model.openStream
import dev.zacsweers.metro.ContributesBinding
import okio.Source
import okio.source
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding

@ContributesBinding(AppGraph::class, binding<DeeplinkContentProvider>())
class AndroidDeeplinkContentProvider @Inject constructor(
    context: Context,
) : DeeplinkContentProvider {
    private val contentResolver = context.contentResolver

    override fun source(deeplinkContent: DeeplinkContent): Source? {
        return deeplinkContent.openStream(contentResolver)?.source()
    }
}
