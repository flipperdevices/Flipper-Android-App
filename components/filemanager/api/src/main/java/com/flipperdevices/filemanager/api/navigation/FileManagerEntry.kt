package com.flipperdevices.filemanager.api.navigation

import com.flipperdevices.core.ui.navigation.ComposableFeatureEntry

private const val DEFAULT_PATH = "/"

interface FileManagerEntry : ComposableFeatureEntry {
    fun fileManagerDestination(path: String = DEFAULT_PATH): String
}
