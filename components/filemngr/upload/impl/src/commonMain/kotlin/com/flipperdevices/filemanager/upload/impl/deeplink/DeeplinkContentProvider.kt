package com.flipperdevices.filemanager.upload.impl.deeplink

import com.flipperdevices.deeplink.model.DeeplinkContent
import okio.Source

interface DeeplinkContentProvider {
    fun source(deeplinkContent: DeeplinkContent): Source?
}
