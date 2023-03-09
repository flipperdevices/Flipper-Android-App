package com.flipperdevices.shake2report.noop

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.shake2report.api.Shake2ReportFeatureEntry
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class Shake2ReportFeatureEntryStub @Inject constructor() : Shake2ReportFeatureEntry {
    override fun start(): String = error("has no stub yet")
    override fun NavGraphBuilder.composable(navController: NavHostController) {
        error("has no stub yet")
    }
}
