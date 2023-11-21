package com.flipperdevices.filemanager.impl.model

import kotlinx.serialization.Serializable

@Serializable
sealed class FileManagerNavigationConfig {
    @Serializable
    data class Screen(val path: String) : FileManagerNavigationConfig()
}