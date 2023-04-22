package com.flipperdevices.infrared.api

import android.net.Uri
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.navigation.FeatureScreenRootRoute
import com.flipperdevices.keyscreen.api.ChooserKeyScreen
import com.squareup.anvil.annotations.ContributesMultibinding
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@ContributesMultibinding(AppGraph::class, ChooserKeyScreen::class)
class ChooserInfraredKeyScreen @Inject constructor() : ChooserKeyScreen {
    private val route = FeatureScreenRootRoute.INFRARED

    override fun isSupported(keyPath: FlipperKeyPath): Boolean {
        if (keyPath.deleted) return false
        return keyPath.path.keyType == FlipperKeyType.INFRARED
    }

    override fun getScreen(keyPath: FlipperKeyPath): String {
        return "@${route.name}?key_path=${Uri.encode(Json.encodeToString(keyPath))}"
    }

    override fun getDeeplink(keyPath: FlipperKeyPath): String? = null
}
