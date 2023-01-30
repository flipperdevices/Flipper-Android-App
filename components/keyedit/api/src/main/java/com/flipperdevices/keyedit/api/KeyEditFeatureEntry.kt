package com.flipperdevices.keyedit.api

import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.ui.navigation.ComposableFeatureEntry
import com.flipperdevices.core.ui.navigation.FeatureScreenRootRoute

interface KeyEditFeatureEntry : ComposableFeatureEntry {
    override val ROUTE: FeatureScreenRootRoute
        get() = FeatureScreenRootRoute.KEY_EDIT_SCREEN

    fun getKeyEditScreen(flipperKeyPath: FlipperKeyPath, title: String?): String
}
