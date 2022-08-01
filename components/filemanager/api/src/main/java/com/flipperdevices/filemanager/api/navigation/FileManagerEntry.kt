package com.flipperdevices.filemanager.api.navigation

import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry

private const val DEFAULT_PATH = "/"

interface FileManagerEntry : AggregateFeatureEntry {
    fun fileManagerDestination(path: String = DEFAULT_PATH): String
}
