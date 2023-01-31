package com.flipperdevices.keyedit

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.keyedit.api.KeyEditFeatureEntry
import com.flipperdevices.keyedit.api.NotSavedFlipperKey
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class KeyEditFeatureEntryNoop @Inject constructor() : KeyEditFeatureEntry {

    override fun getKeyEditScreen(flipperKeyPath: FlipperKeyPath, title: String?): String {
        error("has no stub yet")
    }

    override fun getKeyEditScreen(
        notSavedFlipperKey: NotSavedFlipperKey,
        title: String?
    ): String {
        error("has no stub yet")
    }

    override fun NavGraphBuilder.composable(navController: NavHostController) {
        error("has no stub yet")
    }
}
