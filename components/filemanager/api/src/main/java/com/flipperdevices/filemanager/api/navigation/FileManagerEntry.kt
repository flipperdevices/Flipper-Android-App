package com.flipperdevices.filemanager.api.navigation

import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.deeplink.model.DeeplinkContent

private const val DEFAULT_PATH = "/"

interface FileManagerEntry : AggregateFeatureEntry {
    fun fileManagerDestination(path: String = DEFAULT_PATH): String

    fun uploadFile(
        path: String,
        deeplinkContent: DeeplinkContent
    ): String
}
