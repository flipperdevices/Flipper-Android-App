package com.flipperdevices.share.api

import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.ui.navigation.BottomSheetFeatureEntry
import com.flipperdevices.core.ui.navigation.FeatureScreenRootRoute

interface ShareBottomFeatureEntry : BottomSheetFeatureEntry {
    override val ROUTE: FeatureScreenRootRoute
        get() = FeatureScreenRootRoute.ARCHIVE_SHARE_BOTTOMSHEET

    fun shareDestination(path: FlipperKeyPath?): String
}
