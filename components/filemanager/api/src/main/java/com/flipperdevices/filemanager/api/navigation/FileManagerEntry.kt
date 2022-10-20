package com.flipperdevices.filemanager.api.navigation

import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.core.ui.navigation.FeatureScreenRootRoute

private const val DEFAULT_PATH = "/"

interface FileManagerEntry : AggregateFeatureEntry {
    override val ROUTE: FeatureScreenRootRoute
        get() = FeatureScreenRootRoute.FILE_MANAGER

    fun fileManagerDestination(path: String = DEFAULT_PATH): String
}
