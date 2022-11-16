package com.flipperdevices.share.api

import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.ui.navigation.BottomSheetFeatureEntry

interface ShareBottomFeatureEntry : BottomSheetFeatureEntry {
    fun shareDestination(path: FlipperKeyPath?): String
}
