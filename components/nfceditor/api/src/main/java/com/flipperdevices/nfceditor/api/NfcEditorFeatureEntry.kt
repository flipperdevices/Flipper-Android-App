package com.flipperdevices.nfceditor.api

import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.ui.navigation.ComposableFeatureEntry
import com.flipperdevices.core.ui.navigation.FeatureScreenRootRoute

interface NfcEditorFeatureEntry : ComposableFeatureEntry {
    override val ROUTE: FeatureScreenRootRoute
        get() = FeatureScreenRootRoute.NFC_EDITOR

    fun getNfcEditorScreen(flipperKeyPath: FlipperKeyPath): String
}
