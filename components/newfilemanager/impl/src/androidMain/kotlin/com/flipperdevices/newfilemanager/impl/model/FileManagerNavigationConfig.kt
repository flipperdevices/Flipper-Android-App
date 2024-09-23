package com.flipperdevices.newfilemanager.impl.model

import com.flipperdevices.deeplink.model.DeeplinkContent
import kotlinx.serialization.Serializable

@Serializable
sealed class FileManagerNavigationConfig {
    @Serializable
    data class Screen(val path: String) : FileManagerNavigationConfig()

    @Serializable
    data class Uploading(
        val path: String,
        val deeplinkContent: DeeplinkContent
    ) : FileManagerNavigationConfig()

    @Serializable
    data class Editing(val shareFile: ShareFile) : FileManagerNavigationConfig()

    @Serializable
    data class Download(
        val path: String,
        val shareFile: ShareFile
    ) : FileManagerNavigationConfig()
}
