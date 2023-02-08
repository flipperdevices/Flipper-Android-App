package com.flipperdevices.updater.api

import com.flipperdevices.core.ui.navigation.ComposableFeatureEntry
import com.flipperdevices.core.ui.navigation.FeatureScreenRootRoute
import com.flipperdevices.updater.model.UpdateRequest

interface UpdaterFeatureEntry : ComposableFeatureEntry {
    override val ROUTE: FeatureScreenRootRoute
        get() = FeatureScreenRootRoute.UPDATER

    fun getUpdaterScreen(updateRequest: UpdateRequest? = null): String
}
